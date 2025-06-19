package com.epam.finaltask.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Processing request: {}", request.getRequestURI());
//        final String authHeader = request.getHeader("Authorization");
//        final String tokenParam = request.getParameter("token");
//        final String jwt;
//        final String userEmail;

        // 1. Спроба отримати токен з кук
        String jwt = extractTokenFromCookies(request);

        // 2. Якщо токен не знайдено в куках - перевіряємо параметр та заголовок
        if (jwt == null) {
            final String tokenParam = request.getParameter("token");
            final String authHeader = request.getHeader("Authorization");

            if (tokenParam != null) {
                jwt = tokenParam;
            } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String tokenFromHeader = authHeader.substring(7);
                if (!tokenFromHeader.isBlank()) { // Додано перевірку
                    jwt = tokenFromHeader;
                }
            }
        }

        // 3. Якщо токен так і не знайдено - продовжуємо ланцюжок фільтрів
        if (jwt == null || jwt.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
//        if (tokenParam != null) {
//            jwt = tokenParam;
//        }
//        else if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            log.info("Token: " + authHeader.substring(7));
//            jwt = authHeader.substring(7);
//        } else {
//            filterChain.doFilter(request, response);
//            return;
//        }
        log.info("JWT extracted: {}", jwt);
        final String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("User roles and permissions: {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("JWT authentication successful for: {}", userEmail);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/user/register")
                || path.startsWith("/api/swagger-ui")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/api/v3/api-docs")
                || path.startsWith("/api/oauth2/")
                || path.startsWith("/h2-console/")
                || path.equals("/api/swagger-ui.html");
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.debug("Cookies in request: {}", Arrays.toString(cookies));
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    log.debug("JWT_TOKEN found: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
