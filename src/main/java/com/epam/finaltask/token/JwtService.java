package com.epam.finaltask.token;

import com.epam.finaltask.auth.AuthenticationRequest;
import com.epam.finaltask.auth.AuthenticationService;
import com.epam.finaltask.dto.AuthResponseDTO;
import com.epam.finaltask.exception.JwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    private static final String CLAIM_REFRESH_TOKEN = "isRefreshToken";
    private final UserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.token-expiration-time}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    public JwtService(UserDetailsService userDetailsService,  @Lazy AuthenticationService authenticationService) {
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
    }

    public String generateToken(UserDetails userDetails, boolean isRefreshToken) {
        log.info("User with name: {}, tried to authenticate.", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_REFRESH_TOKEN, isRefreshToken);
        return generateToken(claims, userDetails, isRefreshToken);
    }

    public String generateToken(
            Map<String, Object> extractClaims,
            UserDetails userDetail,
            boolean isRefreshToken) {
        log.info("Generating token for user: {}", userDetail.getUsername());
        Long expirationTime = determineExpirationTime(isRefreshToken);
        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetail.getUsername())
                .claim("roles", userDetail.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody());
    }

    public AuthResponseDTO refreshAccessToken(String refreshToken) {
        try {
            String username = extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (isTokenValid(refreshToken, userDetails, true)) {
                return new AuthResponseDTO(generateToken(userDetails, false));
            } else {
                throw new JwtTokenException(HttpStatus.BAD_REQUEST, "Invalid refresh token");
            }
        } catch (ExpiredJwtException e) {
            throw new JwtTokenException(HttpStatus.UNAUTHORIZED, "Token expired", e);
        } catch (SignatureException e) {
            throw new JwtTokenException(HttpStatus.BAD_REQUEST, "Invalid JWT signature", e);
        }
    }

    private Long determineExpirationTime(boolean isRefreshToken) {
        return isRefreshToken ? refreshTokenExpiration : accessTokenExpiration;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails, boolean isRefreshTokenExpected) {
        final String username = extractUsername(token);
        Object claimRefreshToken = extractAllClaims(token).get(CLAIM_REFRESH_TOKEN);
        boolean isCorrectType = Objects.equals(claimRefreshToken, isRefreshTokenExpected);
        return Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token) && isCorrectType;
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Map<String, String> generateTokens(AuthenticationRequest userDto) {
        log.info("User login attempt for: {}", userDto.getEmail());
        String accessToken = authenticationService.authenticate(userDto);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getEmail());
        String refreshToken = generateToken(userDetails, true);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access-token", accessToken);
        tokens.put("refresh-token", refreshToken);
        return tokens;
    }
}
