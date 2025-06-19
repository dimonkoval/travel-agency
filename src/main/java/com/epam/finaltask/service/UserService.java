package com.epam.finaltask.service;

import java.io.IOException;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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

    void updateUserAvatar(String name, MultipartFile avatar) throws IOException;
}
