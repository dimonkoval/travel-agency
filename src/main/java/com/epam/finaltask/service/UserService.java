package com.epam.finaltask.service;

import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO register(UserDTO userDTO);
    UserDTO updateUser(String username, UserDTO userDTO);
    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    UserResponseDTO getUserProfile(String username);
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
}
