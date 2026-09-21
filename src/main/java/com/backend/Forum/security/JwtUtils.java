package com.backend.Forum.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret-key}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpiration;

    @Value("${jwt.refreshTokenExpirationMs}")
    private int refreshTokenDurationMs;

    public int getJwtExpiration() {
        return jwtExpiration;
    }

    public String generateJwtToken(StudentDetailsImplementation userPrincipal) {
        return generateTokenFromUser(userPrincipal, jwtExpiration);
    }

    public String generateRefreshToken(StudentDetailsImplementation userPrincipal) {
        return generateTokenFromUser(userPrincipal, refreshTokenDurationMs);
    }

    public String generateTokenFromUser(StudentDetailsImplementation userPrincipal, int expiration) {
        String roles = userPrincipal.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.joining(", "));
        return Jwts.builder()
                .subject(userPrincipal.getEmail())
                .claim("roles", roles)
                .claim("id", userPrincipal.getId())
                .claim("fullname", userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key())
                .compact();

    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Claims getClaimsFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
