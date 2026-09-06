package com.campushire.campus_hire.shared.security;

import com.campushire.campus_hire.user.internal.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtTokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs = 900000; // 15 minutes

    public JwtTokenService(@Value("${jwt.secret:defaultSecretKeyThatIsAtLeast32BytesLongForHS256Algorithm}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // --- GENERATION ---

    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail()) // Added so the filter can extract the email
                .claim("role", user.getRole().name())
                .id(UUID.randomUUID().toString()) // The JTI (JWT ID) needed for blacklisting
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(secretKey)
                .compact();
    }

    // --- EXTRACTION ---

    public String extractEmail(String token) {
        // We look specifically for the "email" claim we added during generation
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public String extractJti(String token) {
        // Extracts the unique ID of the token itself
        return extractClaim(token, Claims::getId);
    }

    public long getRemainingExpirationTimeMs(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    // --- VALIDATION ---

    public boolean isTokenValid(String token) {
        try {
            // If the token is expired or tampered with, parseSignedClaims will throw an exception
            return !extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // --- INTERNAL HELPERS ---

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // This is the engine that decodes the JWT and verifies the secure signature
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}