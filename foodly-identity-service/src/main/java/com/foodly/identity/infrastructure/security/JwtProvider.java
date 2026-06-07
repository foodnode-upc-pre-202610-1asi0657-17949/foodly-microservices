package com.foodly.identity.infrastructure.security;

import com.foodly.identity.domain.model.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped // Manejado de forma segura por el contenedor CDI de WildFly
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";

    // Constantes de configuración desacopladas de Spring @Value
    private final String secretKey = "TuClaveSecretaSuperLargaYSeguraParaFirmarLosTokensJWTDeFoodly123!";
    private final String issuer = "foodly-identity-service";
    private final long expirationMinutes = 60;

    public String generateToken(String userId, String email, Set<UserRole> roles) {
        Instant now    = Instant.now();
        Instant expiry = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        List<String> roleNames = roles.stream()
                .map(UserRole::name)
                .collect(Collectors.toList());

        return Jwts.builder()
                .issuer(issuer)
                .subject(userId)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLES, roleNames)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("Token JWT expirado: {}", ex.getMessage());
        } catch (JwtException ex) {
            log.warn("Token JWT inválido: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("Token JWT vacío o nulo: {}", ex.getMessage());
        }
        return false;
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserId(String token) {
        return getClaims(token).getSubject();
    }

    public String getEmail(String token) {
        return getClaims(token).get(CLAIM_EMAIL, String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        return getClaims(token).get(CLAIM_ROLES, List.class);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}