package com.epam.finaltask.config;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.EntityNotFoundException;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.CustomOAuth2UserService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.impl.UserDetailsServiceImpl;
import com.epam.finaltask.token.JwtAuthenticationFilter;
import com.epam.finaltask.token.JwtService;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

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
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter,
                                 @Lazy AuthenticationProvider authenticationProvider,
                                 JwtService jwtService, UserDetailsServiceImpl userDetailsService,
                                 AuthorizationRequestRepository authorizationRequestRepository,
                                 @Lazy UserService userService, @Lazy AuthenticationEntryPoint authenticationEntryPoint,
                                 CustomOAuth2UserService customOAuth2UserService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.userService = userService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.customOAuth2UserService = customOAuth2UserService;
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
                                "/oauth2/**",
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
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .oauth2Login(oauth2 -> oauth2
                                .authorizationEndpoint(authorization -> authorization
                                        .baseUri("/oauth2/authorization")
                                        .authorizationRequestRepository(authorizationRequestRepository)
                                )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/", true)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/api/auth/sign-in");
    }
}
