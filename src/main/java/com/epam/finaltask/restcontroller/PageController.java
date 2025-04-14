package com.epam.finaltask.restcontroller;

import com.epam.finaltask.auth.AuthenticationRequest;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class PageController {
    private final VoucherService voucherService;
    private final UserService userService;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        model.addAttribute("targetUrl", "/");
       return "index";
    }

    @GetMapping("/auth/sign-in")
    public String signIn(Model model) {
        model.addAttribute("loginRequest", new AuthenticationRequest());
        return "auth/sign-in";
    }

    @GetMapping("/user/dashboard")
    public String dashboard(
            Model model,
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) TourType tourType,
            @RequestParam(required = false) HotelType hotelType,
            @RequestParam(required = false) TransferType transferType,
            @RequestParam(required = false) Double price,
            @ModelAttribute("message") String message,
            @ModelAttribute("error") String error
    ) {
        if (principal == null) {
            return "redirect:/auth/sign-in";
        }

        Sort.Direction direction = Sort.Direction.fromString(dir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<VoucherDTO> result = voucherService.searchVouchers(title, tourType, hotelType,  transferType, price, pageable);

        String email = extractEmailFromPrincipal(principal);
        UserDTO user = userService.findUserByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("vouchers", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", result.getSize());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("title", title);
        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("tourType", tourType);
        model.addAttribute("hotelType", hotelType);
        model.addAttribute("transferType", transferType);

        model.addAttribute("price", price);

        if (!message.isEmpty()) {
            model.addAttribute("message", message);
        }else if (!error.isEmpty()) {
            model.addAttribute("message", error);
        }

        return "user/dashboard";
    }

    public String extractEmailFromPrincipal(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauthToken) {
            return oauthToken.getPrincipal().getAttribute("email");
        } else if (principal instanceof UsernamePasswordAuthenticationToken userToken) {
            UserDetails userDetails = (UserDetails) userToken.getPrincipal();
            return userDetails.getUsername();
        }
        throw new IllegalStateException("Невідомий тип аутентифікації: " + principal.getClass().getName());
    }

    private void loadUserData(Model model, String email) {
        UserDTO user = userService.findUserByEmail(email);
        List<VoucherDTO> vouchers = voucherService.findAll();

        model.addAttribute("user", user);
        model.addAttribute("vouchers", vouchers);
    }

    private void handleFlashMessages(Model model, HttpSession session) {

        String sessionMessage = (String) session.getAttribute("message");
        String sessionError = (String) session.getAttribute("error");

        if (sessionMessage != null) {
            model.addAttribute("message", sessionMessage);
            session.removeAttribute("message");
        }

        if (sessionError != null) {
            model.addAttribute("error", sessionError);
            session.removeAttribute("error");
        }
    }
}
