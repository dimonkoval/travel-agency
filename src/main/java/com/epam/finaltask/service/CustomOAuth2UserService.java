package com.epam.finaltask.service;

import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found in OAuth2 provider response");
        }

        userRepository.findUserByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setActive(true);
                    newUser.setPhoneNumber("000000000000");
                    newUser.isEnabled();
                    newUser.isAccountNonExpired();
                    newUser.isAccountNonLocked();
                    newUser.isAccountNonExpired();
                    newUser.setRole((Role.USER));
                    return userRepository.save(newUser);
                });

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oauth2User.getAttributes(),
                "email"
        );
    }
}

