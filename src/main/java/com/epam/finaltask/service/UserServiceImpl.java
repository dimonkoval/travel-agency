package com.epam.finaltask.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.UserProfileDTO;
import com.epam.finaltask.dto.UserResponseDTO;
import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.dto.groups.OAuthGroup;
import com.epam.finaltask.dto.groups.PasswordGroup;
import com.epam.finaltask.exception.EntityAlreadyExistsException;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.mapper.UserVoucherMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.UserVoucherRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final VoucherService voucherService;
	private final UserVoucherRepository userVoucherRepository;
	private final UserVoucherMapper userVoucherMapper;
	@Qualifier("validator")
	private final Validator validator;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, VoucherService voucherService, UserVoucherRepository userVoucherRepository, UserVoucherMapper userVoucherMapper, Validator validator) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.voucherService = voucherService;
        this.userVoucherRepository = userVoucherRepository;
        this.userVoucherMapper = userVoucherMapper;
        this.validator = validator;
    }

	@Override
	@Transactional
	public UserDTO register(UserDTO userDTO, boolean isOAuth) {
		log.info("Registering user with username: {}", userDTO.getUsername());
		if (userRepository.existsByUsername(userDTO.getUsername())) {
			throw new EntityAlreadyExistsException(ENTITY_ALREADY_EXISTS);
		}

		Set<ConstraintViolation<UserDTO>> violations = isOAuth ?
				validator.validate(userDTO, OAuthGroup.class) :
				validator.validate(userDTO, PasswordGroup.class);

		if (!violations.isEmpty()) {
			String errorMessage = violations.stream()
					.map(ConstraintViolation::getMessage)
					.collect(Collectors.joining(", "));
			throw new IllegalArgumentException("Validation failed: " + errorMessage);
		}

		User user = userMapper.toUser(userDTO);

		if (!isOAuth) {
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}

		user = userRepository.save(user);
		return userMapper.toUserDTO(user);
	}

	@Override
	@Transactional
	public UserDTO updateUser(String username, UserDTO userDTO, boolean isOAuth) {
		log.info("Updating user with username: {}", username);

		User user = userRepository.findById(UUID.fromString(userDTO.getId()))
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

		Set<ConstraintViolation<UserDTO>> violations;
		if (isOAuth) {
			violations = validator.validate(userDTO, OAuthGroup.class);
		} else {
			userDTO.setPassword(user.getPassword());
			violations = validator.validate(userDTO, PasswordGroup.class);
		}

		if (!violations.isEmpty()) {
			for (ConstraintViolation<UserDTO> violation : violations) {
				log.info("Error for validation {} ", violation.getMessage());
			}
			return null;
		}

		userMapper.updateUserFromDTO(userDTO, user);

		if (!isOAuth && userDTO.getPassword() != null) {
			user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Оновлюємо пароль, якщо це не OAuth
		}

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
	public UserProfileDTO getUserProfile(String email) {
		log.info("Fetching profile for user: {}", email);
		User user = userRepository.findUserByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));

		List<UserVoucherDTO> userVouchers = userVoucherRepository.findAllByUserId(user.getId())
				.stream()
				.map(userVoucherMapper::toUserVoucherDTO)
				.toList();

		return UserProfileDTO.builder()
				.id(user.getId().toString())
				.username(user.getUsername())
				.email(user.getEmail())
				.balance(user.getBalance())
				.vouchers(userVouchers)
				.role(user.getRole())
				.build();
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

	@Override
	public UserDTO findUserByEmail(String email) {
		User user = userRepository.findUserByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
		return userMapper.toUserDTO(user);
	}

	@Override
	public void deleteUser(String id) {
		User user = userRepository.findById(UUID.fromString(id))
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
		userRepository.delete(user);
	}

	public void toggleUserStatus(UUID userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(ENTITY_NOT_FOUND));
		user.setActive(!user.getActive());
		userRepository.save(user);
	}
}
