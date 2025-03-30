package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.EntityAlreadyExistsException;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.epam.finaltask.exception.StatusCodes.ENTITY_ALREADY_EXISTS;
import static com.epam.finaltask.exception.StatusCodes.ENTITY_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final VoucherService voucherService;

	@Override
	@Transactional
	public UserDTO register(UserDTO userDTO) {
		log.info("Registering user with username: {}", userDTO.getUsername());

		if (userRepository.existsByUsername(userDTO.getUsername())) {
			throw new EntityAlreadyExistsException(ENTITY_ALREADY_EXISTS);
		}

		User user = userMapper.toUser(userDTO);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user = userRepository.save(user);

		return userMapper.toUserDTO(user);
	}

	@Override
	@Transactional
	public UserDTO updateUser(String username, UserDTO userDTO) {
		log.info("Updating user with username: {}", username);
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
		userMapper.updateUserFromDTO(userDTO, user);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		return userMapper.toUserDTO(userRepository.save(user));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDTO getUserByUsername(String username) {
		log.info("Fetching user by username: {}", username);
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
		return userMapper.toUserDTO(user);
	}

	@Override
	@Transactional
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		log.info("Changing account status for user: {}", userDTO.getUsername());
		userRepository.findById(UUID.fromString(userDTO.getId()))
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
		User updatedUser = userMapper.toUser(userDTO);
		updatedUser.setActive(userDTO.isActive());
		User savedUser = userRepository.save(updatedUser);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDTO getUserById(UUID id) {
		log.info("Fetching user by ID: {}", id);
		User user = userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
		return userMapper.toUserDTO(user);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponseDTO getUserProfile(String email) {
		log.info("Fetching profile for user: {}", email);
		User user = userRepository.findUserByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

		List<VoucherDTO> vouchers = voucherService.findAllByUserId(user.getId().toString());

		return userMapper.toUserResponseDTO(user, vouchers);
	}

	@Override
	public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
		Page<User> userPage = userRepository.findAll(pageable);
		List<UserResponseDTO> userDTOs = userPage
				.stream()
				.map((User user) -> userMapper.toUserResponseDTO(user,
						voucherService.findAllByUserId(user.getId().toString())))
				.collect(Collectors.toList());
		return new PageImpl<>(userDTOs, pageable, userPage.getTotalElements());
	}
}
