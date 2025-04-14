package com.epam.finaltask.restcontroller;

import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import java.security.Principal;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageControllerTest {

    private VoucherService voucherService;
    private UserService userService;
    private PageController pageController;

    @BeforeEach
    void setUp() {
        voucherService = mock(VoucherService.class);
        userService = mock(UserService.class);
        pageController = new PageController(voucherService, userService);
    }

    @Test
    void testDashboardWhenPrincipalIsNull() {
        Model model = mock(Model.class);
        Principal principal = null;

        String view = pageController.dashboard(model, principal, 0, 10, "id", "asc", null, null, null, null, null, "", "");

        assertEquals("redirect:/auth/sign-in", view);
    }

    @Test
    void testExtractEmailFromPrincipalOAuth2Authentication() {
        // Arrange
        OAuth2AuthenticationToken oauthToken = mock(OAuth2AuthenticationToken.class);
        OAuth2User oauthUser = mock(OAuth2User.class);
        when(oauthToken.getPrincipal()).thenReturn(oauthUser);
        when(oauthUser.getAttribute("email")).thenReturn("test@example.com");

        // Act
        String email = pageController.extractEmailFromPrincipal(oauthToken);

        // Assert
        assertEquals("test@example.com", email);
    }

    @Test
    void testExtractEmailFromPrincipalUsernamePasswordAuthentication() {
        // Arrange
        UsernamePasswordAuthenticationToken userToken = mock(UsernamePasswordAuthenticationToken.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(userToken.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");

        // Act
        String email = pageController.extractEmailFromPrincipal(userToken);

        // Assert
        assertEquals("test@example.com", email);
    }

    @Test
    void testExtractEmailFromPrincipalUnknownAuthentication() {
        // Arrange
        Principal principal = mock(Principal.class);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> pageController.extractEmailFromPrincipal(principal));
    }
}
