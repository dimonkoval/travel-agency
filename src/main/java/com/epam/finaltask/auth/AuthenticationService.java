package com.epam.finaltask.auth;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.token.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    public UserDTO register(UserDTO userDTO) {
        try {
            User user = User.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .role(Role.ADMIN)
                    .phoneNumber(userDTO.getPhoneNumber())
                    .balance(BigDecimal.valueOf(5000))
                    .active(true)
                    .email(userDTO.getEmail())
                    .build();
            userRepository.save(user);
            return userMapper.toUserDTO(user);
        } catch (DataIntegrityViolationException ex) {
            log.error("Registration error: User with this email or phone number already exists", ex);
            throw new IllegalStateException(ex.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error during registration", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    public String authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            User user = userRepository.findUserByEmail(request.getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return jwtService.generateToken(user, false);
        } catch (BadCredentialsException ex) {
            log.warn("Invalid login attempt for user: {}", request.getEmail());
            throw new IllegalArgumentException(ex.getMessage());
        } catch (EntityNotFoundException ex) {
            log.warn("User {} not found", request.getEmail());
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error during authentication", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    public boolean userExists(String email) {
        return userRepository.findUserByEmail(email).isPresent();
    }

    public boolean isUserBlocked(String email) {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user != null && !user.getActive();
    }
}
