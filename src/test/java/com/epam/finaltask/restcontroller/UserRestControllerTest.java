package com.epam.finaltask.restcontroller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getAllUsers_ShouldReturnPageOfUsersForAdmin() {

        Page<UserResponseDTO> page = new PageImpl<>(Collections.singletonList(new UserResponseDTO()));
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(page);

        ResponseEntity<Page<UserResponseDTO>> response = userRestController.getAllUsers(0, 10, "id");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1, response.getBody().getContent().size());
        verify(userService).getAllUsers(PageRequest.of(0, 10, Sort.by("id")));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserByUsername_ShouldReturnUserForAdmin() {

        UserDTO expectedUser = new UserDTO();
        expectedUser.setEmail("admin@example.com");
        when(userService.getUserByUsername("admin@example.com")).thenReturn(expectedUser);

        ResponseEntity<UserDTO> response = userRestController.getUserByUsername("admin@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("admin@example.com", response.getBody().getEmail());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void changeAccountStatus_ShouldUpdateStatusAndReturnUserForAdmin() {

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("user@example.com");
        when(userService.changeAccountStatus(any(UserDTO.class))).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userRestController.changeAccountStatus(userDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).changeAccountStatus(userDTO);
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void getUserById_ShouldReturnUserForAdmin() {
        UUID userId = UUID.randomUUID();
        UserDTO expectedUser = new UserDTO();
        expectedUser.setId(userId.toString());

        when(userService.getUserById(userId)).thenReturn(expectedUser);

        ResponseEntity<UserDTO> response = userRestController.getUserById(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId.toString(), response.getBody().getId());

    }
}