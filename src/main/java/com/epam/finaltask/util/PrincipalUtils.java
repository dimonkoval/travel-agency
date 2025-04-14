package com.epam.finaltask.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;

@UtilityClass
public class PrincipalUtils {

    public  String extractEmail(Principal principal) {
        if (principal instanceof OAuth2AuthenticationToken oauthToken) {
            return oauthToken.getPrincipal().getAttribute("email");
        } else if (principal instanceof UsernamePasswordAuthenticationToken userToken) {
            UserDetails userDetails = (UserDetails) userToken.getPrincipal();
            return userDetails.getUsername();
        }
        throw new IllegalStateException("Невідомий тип аутентифікації: " + principal.getClass().getName());
    }
}
