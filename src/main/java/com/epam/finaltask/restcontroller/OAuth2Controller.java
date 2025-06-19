package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.AuthResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("api/oauth2")
public class OAuth2Controller {
    @Operation(summary = "Handle successful OAuth2 authentication and return the access token.")
    @GetMapping("/success")
    public ResponseEntity<AuthResponseDTO> oauth2Success(@RequestParam String token) {
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    @Operation(summary = "Handle failed OAuth2 authentication and return an error message.")
    @GetMapping("/failure")
    public ResponseEntity<String> oauth2Failure(HttpSession session) {
        String error = (String) session.getAttribute("OAUTH2_ERROR");
        session.removeAttribute("OAUTH2_ERROR");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("OAuth2 authentication failed: " + error);
    }
}
