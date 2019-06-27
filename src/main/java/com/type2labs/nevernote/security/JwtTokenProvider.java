package com.type2labs.nevernote.security;

import com.type2labs.nevernote.exception.BadRequestException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

// TODO: Clear up class
@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationTime;

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(Long.toString(((UserPrincipal) authentication.getPrincipal()).getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException | MalformedJwtException ignored) {
            throw new BadRequestException("Malformed JWT");
        }
    }
}
