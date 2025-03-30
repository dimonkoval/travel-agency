package com.epam.finaltask.restcontroller;

import com.epam.finaltask.annotation.swagger.RestResponse;
import com.epam.finaltask.auth.AuthenticationRequest;
import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.dto.AuthResponseDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.token.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/auth")
public class AuthenticationRestController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Operation(summary = "Register a new user and return user details.")
    @RestResponse
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserDTO userDTO) {
        log.info("Register request received: {}", userDTO);
        return ResponseEntity.ok(authenticationService.register(userDTO));
    }

    @Operation(summary = "Authenticate user and return access token.")
    @RestResponse
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginV2(@RequestBody AuthenticationRequest requestDto) {
        Map<String, String> tokens = jwtService.generateTokens(requestDto);
        return ResponseEntity.ok(tokens);
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
