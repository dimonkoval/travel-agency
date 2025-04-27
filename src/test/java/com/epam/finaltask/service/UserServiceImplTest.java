package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void getUserByUsername_UserExists_Success() {

    String username = "existingUser";
    User user = new User();
    user.setUsername(username);

    UserDTO expectedUserDTO = new UserDTO();
    expectedUserDTO.setUsername(username);

    when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
    when(userMapper.toUserDTO(any(User.class))).thenReturn(expectedUserDTO);

    UserDTO result = userService.getUserByUsername(username);

    assertNotNull(result, "The returned UserDTO should not be null");
    assertEquals(expectedUserDTO.getUsername(), result.getUsername(),
        "The username should match the expected value");

    verify(userRepository, times(1)).findUserByUsername(username);
    verify(userMapper, times(1)).toUserDTO(any(User.class));
  }

  @Test
  void changeAccountStatus_UserExist_Success() {
    String userId = UUID.randomUUID().toString();
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    userDTO.setActive(true);

    User user = new User();
    user.setId(UUID.fromString(userId));
    user.setActive(false);

    User updatedUser = new User();
    updatedUser.setId(UUID.fromString(userId));
    updatedUser.setActive(true);

    when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));
    when(userMapper.toUser(any(UserDTO.class))).thenReturn(updatedUser);
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    when(userMapper.toUserDTO(any(User.class))).thenReturn(userDTO);

    UserDTO resultDTO = userService.changeAccountStatus(userDTO);

    assertNotNull(resultDTO, "The returned UserDTO should not be null");
    assertTrue(resultDTO.isActive(), "The account status should be updated to true");

    verify(userRepository, times(1)).findById(UUID.fromString(userId));
    verify(userRepository, times(1)).save(any(User.class));
  }


  @Test
  void getUserById_UserExist_Success() {

    UUID id = UUID.randomUUID();
    User user = new User();
    user.setId(id);

    UserDTO expectedUserDTO = new UserDTO();
    expectedUserDTO.setId(id.toString());

    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    when(userMapper.toUserDTO(any(User.class))).thenReturn(expectedUserDTO);

    UserDTO resultDTO = userService.getUserById(id);

    assertNotNull(resultDTO, "The returned UserDTO should not be null");
    assertEquals(expectedUserDTO.getId(), resultDTO.getId(),
        "The user ID should match the expected value");

    verify(userRepository, times(1)).findById(id);
    verify(userMapper, times(1)).toUserDTO(any(User.class));
  }

}
