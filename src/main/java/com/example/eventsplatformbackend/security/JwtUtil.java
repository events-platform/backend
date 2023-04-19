package com.example.eventsplatformbackend.security;

import com.example.eventsplatformbackend.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtil {
    @Value("${jwt.access-secret}")
    private String accessSecret;
    @Value("${jwt.refresh-secret}")
    private String refreshSecret;
    @Value("${jwt.expiration-time}")
    private String expirationTime;

    public Claims getClaimsFromToken(String authToken) {
        String key = Base64.getEncoder().encodeToString(accessSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
                .getBody();
    }

    public Long extractId(String authToken) {
        return Long.valueOf(getClaimsFromToken(authToken)
                .getSubject());
    }

    public boolean validateToken(String authToken) {
        return getClaimsFromToken(authToken)
                .getExpiration()
                .after(new Date());
    }

    public String generateAccessToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());

        long expirationSeconds = Long.parseLong(expirationTime);
        Date creationDate = new Date();
        Date expirationDate = new Date(creationDate.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(accessSecret.getBytes()))
                .compact();
    }

    public String generateRefreshToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();

        long expirationSeconds = Long.parseLong(expirationTime);
        Date creationDate = new Date();
        // TODO сделать рефреш токен одноразовым
        Date expirationDate = new Date(creationDate.getTime() + expirationSeconds * 1000 * 30);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(creationDate)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(refreshSecret.getBytes()))
                .compact();
    }
}
