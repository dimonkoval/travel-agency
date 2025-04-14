package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listUsers_ShouldReturnAdminView() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username"));
        Page<UserResponseDTO> page = new PageImpl<>(Collections.emptyList());
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);
        when(principal.getName()).thenReturn("admin@test.com");
        UserDTO currentUser = new UserDTO();
        when(userService.findUserByEmail(anyString())).thenReturn(currentUser);

        // Act
        String viewName = adminController.listUsers(model, principal, 0, 10);

        // Assert
        assertEquals("user/admin", viewName);
        verify(model).addAttribute(eq("users"), anyList());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", 1);
        verify(model).addAttribute("size", 10);
        verify(model).addAttribute("user", currentUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showCreateForm_ShouldReturnFormView() {
        // Act
        String viewName = adminController.showCreateForm(model);

        // Assert
        assertEquals("user/form", viewName);
        verify(model).addAttribute(eq("user"), any(UserDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_ShouldRedirectToList() {
        // Arrange
        UserDTO userDTO = new UserDTO();

        // Act
        String viewName = adminController.createUser(userDTO);

        // Assert
        assertEquals("redirect:/api/admin/users", viewName);
        verify(userService).register(eq(userDTO), eq(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void showEditForm_ShouldReturnFormViewWithUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        when(userService.getUserById(any(UUID.class))).thenReturn(userDTO);

        // Act
        String viewName = adminController.showEditForm(userId.toString(), model);

        // Assert
        assertEquals("user/form", viewName);
        verify(model).addAttribute("user", userDTO);
        verify(model).addAttribute("roles", Role.values());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Success_ShouldAddSuccessMessage() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        UserDTO userDTO = new UserDTO();

        // Act
        String viewName = adminController.updateUser(userId, userDTO, redirectAttributes);

        // Assert
        assertEquals("redirect:/api/admin/users", viewName);
        verify(userService).updateUser(eq(userId), eq(userDTO), eq(false));
        verify(redirectAttributes).addFlashAttribute("successMessage", "User updated successfully");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Exception_ShouldAddErrorMessage() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        UserDTO userDTO = new UserDTO();
        doThrow(new RuntimeException("Test error")).when(userService).updateUser(anyString(), any(), anyBoolean());

        // Act
        String viewName = adminController.updateUser(userId, userDTO, redirectAttributes);

        // Assert
        assertEquals("redirect:/api/admin/users", viewName);
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Error updating user: Test error");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_ShouldDeleteWhenNotSelf() throws Exception {
        // Arrange
        String userId = UUID.randomUUID().toString();
        UserDTO currentUser = new UserDTO();
        currentUser.setUsername("admin@test.com");
        when(principal.getName()).thenReturn("admin@test.com");
        when(userService.getUserByUsername(anyString())).thenReturn(currentUser);
        UserDTO userToDelete = new UserDTO();
        userToDelete.setUsername("other@test.com");
        when(userService.getUserById(any(UUID.class))).thenReturn(userToDelete);

        // Act & Assert
        mockMvc.perform(post("/api/admin/users/delete/" + userId)
                        .principal(principal))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/admin/users"));

        verify(userService).deleteUser(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_SelfDeletion_ShouldThrowException() {
        // Arrange
        String userId = UUID.randomUUID().toString();
        UserDTO currentUser = new UserDTO();
        currentUser.setUsername("admin@test.com");
        when(principal.getName()).thenReturn("admin@test.com");
        when(userService.getUserByUsername(anyString())).thenReturn(currentUser);
        when(userService.getUserById(any(UUID.class))).thenReturn(currentUser);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                adminController.deleteUser(userId, principal));
    }
}
