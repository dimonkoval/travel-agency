package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.util.PrincipalUtils;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import java.security.Principal;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@SessionAttributes({"message", "error"})
@RequestMapping("/api/users")
public class UserRestController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile(Principal principal) {
        UserProfileDTO userProfile = userService.getUserProfile(PrincipalUtils.extractEmail(principal));
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @RequestBody @Valid UserDTO userDTO) {
        log.info("Updating user: {}", username);
        return ResponseEntity.ok(userService.updateUser(username, userDTO, false));
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user by email: {}", username);
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PatchMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeAccountStatus(@RequestBody UserDTO userDTO) {
        log.info("Changing account status for user: {}", userDTO.getUsername());
        return ResponseEntity.ok(userService.changeAccountStatus(userDTO));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        log.info("Fetching user by ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/profile/{username}")
    public String getUserProfile(@PathVariable String username, Model model) {
        log.info("Fetching profile for user: {}", username);
        UserProfileDTO userProfile = userService.getUserProfile(username);
        model.addAttribute("user", userProfile);
        return "user/profile";
    }
}
