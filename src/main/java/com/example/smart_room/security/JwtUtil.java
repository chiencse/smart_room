package com.example.smart_room.security;

import com.example.smart_room.model.User;
import com.example.smart_room.response.PayloadJwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private String SECRET_KEY = "thisis_a_strong_secret_key_123456";
    private long JWT_TOKEN_VALIDITY = 60 * 60; // 1 hour in seconds

    // Retrieve username from JWT token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Retrieve expiration date from JWT token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // For retrieving any information from token, we need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // Generate token for user
    public String generateToken(User user) {
        PayloadJwt payload = new PayloadJwt(user.getUsername(), user.getRoles(), user.getId(), user.getEmail());
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", payload.getUsername());
        claims.put("roles", payload.getRoles().stream().map(role -> "ROLE_" + role).collect(Collectors.toSet()));
        if (payload.getId() != null) claims.put("id", payload.getId());
        if (payload.getEmail() != null) claims.put("email", payload.getEmail());
        return doGenerateToken(claims, user.getUsername()); // Pass username as subject
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    public PayloadJwt extractPayload(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return new PayloadJwt(
                claims.get("username", String.class),
                claims.get("roles", Set.class), // Assuming roles are stored as a Set in the token
                claims.get("id", Long.class),
                claims.get("email", String.class)
        );
    }
}