package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import java.security.Principal;
import java.util.Collections;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;

    @Test
    void getProfile_ShouldReturnUserProfileWhenPrincipalProvided() {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@example.com");

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setEmail("test@example.com");
        when(userService.getUserProfile("test@example.com")).thenReturn(expectedResponse);

        ResponseEntity<UserResponseDTO> response = userRestController.getProfile(principal);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getAllUsers_ShouldReturnPageOfUsersForAdmin() {

        Page<UserResponseDTO> page = new PageImpl<>(Collections.singletonList(new UserResponseDTO()));
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(page);

        ResponseEntity<Page<UserResponseDTO>> response = userRestController.getAllUsers(0, 10, "id");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getContent().size());
        verify(userService).getAllUsers(PageRequest.of(0, 10, Sort.by("id")));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void updateUser_ShouldUpdateUserAndReturnDTOForAdmin() {

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("updated@example.com");
        when(userService.updateUser(eq("test@example.com"), any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userRestController.updateUser("test@example.com", userDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("updated@example.com", response.getBody().getEmail());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserByUsername_ShouldReturnUserForAdmin() {

        UserDTO expectedUser = new UserDTO();
        expectedUser.setEmail("admin@example.com");
        when(userService.getUserByUsername("admin@example.com")).thenReturn(expectedUser);

        ResponseEntity<UserDTO> response = userRestController.getUserByUsername("admin@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("admin@example.com", response.getBody().getEmail());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void changeAccountStatus_ShouldUpdateStatusAndReturnUserForAdmin() {

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("user@example.com");
        when(userService.changeAccountStatus(any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userRestController.changeAccountStatus(userDTO);

        assertEquals(200, response.getStatusCodeValue());
        verify(userService).changeAccountStatus(userDTO);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserById_ShouldReturnUserForAdmin() {
        UUID userId = UUID.randomUUID();
        UserDTO expectedUser = new UserDTO();
        expectedUser.setId(userId.toString()); // Конвертуємо в String

        when(userService.getUserById(userId)).thenReturn(expectedUser);

        ResponseEntity<UserDTO> response = userRestController.getUserById(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userId.toString(), response.getBody().getId()); // Порівнюємо String з String
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserProfile_ShouldReturnProfileForAdmin() {

        UserResponseDTO expectedProfile = new UserResponseDTO();
        expectedProfile.setEmail("profile@example.com");
        when(userService.getUserProfile("profile@example.com")).thenReturn(expectedProfile);

        ResponseEntity<UserResponseDTO> response = userRestController.getUserProfile("profile@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("profile@example.com", response.getBody().getEmail());
    }
}