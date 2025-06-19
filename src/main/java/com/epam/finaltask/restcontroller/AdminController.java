package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model,
                            Principal principal,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username"));
        Page<UserResponseDTO> userPage = userService.getAllUsers(pageable);

        UserDTO currentUser = userService.findUserByEmail(principal.getName());
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("user", currentUser);
        return "user/admin";
    }

    @GetMapping("/users/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "user/form";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("user") UserDTO userDTO) {
        userService.register(userDTO, false);
        return "redirect:/api/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        UserDTO userDTO = userService.getUserById(UUID.fromString(id));
        model.addAttribute("user", userDTO);
        model.addAttribute("roles", Role.values());
        return "user/form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable String id,
                             @ModelAttribute("user") UserDTO userDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, userDTO, false);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
        }
        return "redirect:/api/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable String id, Principal principal) {
        UserDTO currentUser = userService.getUserByUsername(principal.getName());
        UserDTO userToDelete = userService.getUserById(UUID.fromString(id));

        if (currentUser.getUsername().equals(userToDelete.getUsername())) {
            throw new IllegalStateException("You cannot delete your own account");
        }

        userService.deleteUser(id);
        return "redirect:/api/admin/users";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable String id) {
        userService.toggleUserStatus(UUID.fromString(id));
        return "redirect:/api/admin/users";
    }
}