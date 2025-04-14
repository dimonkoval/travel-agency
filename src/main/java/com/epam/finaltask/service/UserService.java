package com.epam.finaltask.service;

import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO register(UserDTO userDTO, boolean isOAuth);
    UserDTO updateUser(String username, UserDTO userDTO, boolean isOAuth);
    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    UserProfileDTO getUserProfile(String username);
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserDTO findUserByEmail(String email);
    void deleteUser(String id);

    void toggleUserStatus(UUID id);
}
