package no.hvl.group17.feedapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private final String SECRET = "THIS_IS_A_LONG_SECURE_SECRET_32_BYTES_MINIMUM";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
    .verifyWith(key)
    .build()
    .parseSignedClaims(token)
    .getPayload();

        return resolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 3600 * 24))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername());
    }
}
