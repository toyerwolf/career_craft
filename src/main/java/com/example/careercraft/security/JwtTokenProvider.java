package com.example.careercraft.security;

import com.example.careercraft.dto.JwtResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationInMs}")
    private Long jwtExpirationInMs;

    @Value("${refreshTokenExpiration}")
    private Long jwtExpirationForNewToken;

    public JwtResponse generateTokens(UserPrincipal userPrincipal) {
        logger.info("Generating tokens with jwtExpirationInMs: {} and jwtExpirationForNewToken: {}", jwtExpirationInMs, jwtExpirationForNewToken);

        String accessToken = generateAccessToken(userPrincipal);
        String refreshToken = generateRefreshToken(userPrincipal);
        return new JwtResponse(accessToken, refreshToken);
    }

    private String generateAccessToken(UserPrincipal userPrincipal) {
        return buildToken(userPrincipal.getId(), jwtExpirationInMs);
    }

    private String generateRefreshToken(UserPrincipal userPrincipal) {
        return buildToken(userPrincipal.getId(), jwtExpirationForNewToken);
    }

    private String buildToken(Long userId, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        logger.info("Building token with expirationTime: {} -> now: {} -> expiryDate: {}", expirationTime, now, expiryDate);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String generateTokenFromEmail(String email) {
        return buildTokenFromEmail(email, jwtExpirationInMs);
    }

    public String generateRefreshTokenFromUsername(String username) {
        return buildTokenFromEmail(username, jwtExpirationForNewToken);
    }

    private String buildTokenFromEmail(String email, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);
        logger.info("Building token from email with expirationTime: {} -> now: {} -> expiryDate: {}", expirationTime, now, expiryDate);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public String getUserEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        throw new RuntimeException("Invalid JWT token");
    }
}