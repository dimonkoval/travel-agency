package com.epam.finaltask.service;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.UUID;

public class OAuth2UserDetails extends org.springframework.security.core.userdetails.User {
    public OAuth2UserDetails(String username, String password) {
        super(username,
                password,
                true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}