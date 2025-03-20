package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.User;

public interface UserMapper {
    User toUser(UserDTO userDTO);
    UserDTO toUserDTO(User user);
}
