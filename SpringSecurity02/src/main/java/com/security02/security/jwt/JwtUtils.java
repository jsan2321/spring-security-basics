package com.security02.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret.key}") // getting the correct property value with: "${...}"
    private String secretKey; // method signing (token)

    @Value("${jwt.time.expiration}")
    private String timeExpiration; // how long token validation last

    // generate access token
    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256) // double encryption
                .compact();
    }

    // validate access token
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            log.error("Token no valid, error: ".concat(e.getMessage()));
            return false;
        }
    }

    // get token username
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    // get one claim
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {
        Claims claims = extracAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    // get all token claims
    public Claims extracAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // get token signature
    public Key getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
