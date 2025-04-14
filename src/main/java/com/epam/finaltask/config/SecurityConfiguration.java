package com.epam.finaltask.config;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.impl.UserDetailsServiceImpl;
import com.epam.finaltask.token.JwtAuthenticationFilter;
import com.epam.finaltask.token.JwtService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtService  jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    private final UserService userService;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, @Lazy AuthenticationProvider authenticationProvider, JwtService jwtService, UserDetailsServiceImpl userDetailsService, AuthorizationRequestRepository authorizationRequestRepository, @Lazy UserService userService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'"))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/api/auth/**",
                                "/api/oauth2/**",
                                "/login/oauth2/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**",
                                "/css/**",
                                "/js/**",
                                "/",
                                "/auth/sign-in",
                                "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .oauth2Login(oauth2 -> oauth2
//                                .authorizationEndpoint(authorization -> authorization
//                                        .baseUri("/oauth2/authorize")
//                                        .authorizationRequestRepository(authorizationRequestRepository)
//                                )
//                        .successHandler((request, response, authentication) -> {
//                            log.info("OAuth2 successful. Principal: {}", authentication.getPrincipal());
//                            OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//                            UserDetails userDetails;
//                            try {
//                                userDetails = userDetailsService.loadUserByUsername(oauthUser.getAttribute("email"));
//                            } catch (UsernameNotFoundException e) {
//                                String email = oauthUser.getAttribute("email");
//                                String name = oauthUser.getAttribute("name");
//                                userDetails = findOrCreateOAuthUser(email, name);
//                            }
//
//                            String token = jwtService.generateToken(userDetails, false);
//                            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
//                            jwtCookie.setHttpOnly(true);
//                            jwtCookie.setPath("/");
//                            jwtCookie.setMaxAge(86400);
//                            response.addCookie(jwtCookie);
//
//                            response.sendRedirect("/user/dashboard");
//                        })
//                                .failureHandler((request, response, exception) -> {
//                                    log.error("OAuth2 error: {}", exception.getMessage(), exception);
//                                    request.getSession().setAttribute("OAUTH2_ERROR", exception.getMessage());
//                                    response.sendRedirect("/api/oauth2/failure");
//                                })
//                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JWT_TOKEN")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("http://localhost:8080");
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "PUT", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    public UserDetails findOrCreateOAuthUser(String email, String name){
        UserDTO user;
        try {
            userService.getUserByUsername(email);
        } catch (EntityNotFoundException e){
            user = new UserDTO();
            user.setEmail(email);
            user.setUsername(name);
            user.setRole(Role.USER);
            user.setActive(true);
            String randomPassword = UUID.randomUUID().toString();
            user.setPassword(randomPassword);
            userService.register(user, true);
        }
        return userDetailsService.loadUserByOAuth2(email);
    }
}
