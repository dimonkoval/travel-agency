package com.epam.finaltask.restcontroller;

import com.epam.finaltask.annotation.swagger.RestResponse;
import com.epam.finaltask.auth.AuthenticationRequest;
import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.dto.AuthResponseDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.token.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("api/auth")
public class AuthenticationRestController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final MessageSource messageSource;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "auth/sign-up";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {



        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();

            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

            log.info("Error for register user: {}",errorMessage);
            return "auth/sign-up";
        }

        authenticationService.register(userDTO);

        redirectAttributes.addFlashAttribute("message", "Successfully registered!");
        return "redirect:/api/auth/sign-in";
    }

    @GetMapping("/sign-in")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new AuthenticationRequest());
        }
        return "auth/sign-in";
    }

    @Operation(summary = "Authenticate user and return access token.")
    @RestResponse
    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") AuthenticationRequest loginRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Locale locale,
            HttpServletResponse response
    ) {
        try {
        log.info("Login attempt: email={}", loginRequest.getEmail());
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("loginRequest", loginRequest);
            redirectAttributes.addAttribute("error", true);
            return "redirect:/auth/sign-in";
        }

        if (!authenticationService.userExists(loginRequest.getEmail())) {
            log.warn("User not found: {}", loginRequest.getEmail());
            return "redirect:/auth/sign-in?error=true";
        }

        if (authenticationService.isUserBlocked(loginRequest.getEmail())) {
            log.warn("Blocked user login attempt: {}", loginRequest.getEmail());
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("error.account.blocked", null, locale));
            return "redirect:/auth/sign-in?error=blocked";
        }


        Map<String, String> tokens = jwtService.generateTokens(loginRequest);
        Cookie cookie = new Cookie("JWT_TOKEN", tokens.get("access-token"));
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        return "redirect:/api/users/profile/" + loginRequest.getEmail();
        } catch (BadCredentialsException e) {
            log.error("Bad credentials for email: {}", loginRequest.getEmail());
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.error.bad-credentials", null, locale));
            return "redirect:/auth/sign-in";

        } catch (Exception e) {
            log.error("Login error", e);
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("auth.error.generic", null, locale));
            return "redirect:/auth/sign-in";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        new SecurityContextLogoutHandler().logout(request, response, null);
        return "redirect:/";
    }

    @Operation(summary = "Accepts a refresh token and returns a new access token.")
    @RestResponse
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        log.info("Received refresh token: {}", refreshToken);
        return ResponseEntity.ok(jwtService.refreshAccessToken(refreshToken));
    }
}
