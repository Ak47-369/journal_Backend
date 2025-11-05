package com.myjournal.journalApp.service;

import com.myjournal.journalApp.configuration.JwtConfig;
import com.myjournal.journalApp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtTokenService {
    private final JwtConfig jwtConfig;

    public JwtTokenService(JwtConfig jwtConfig){
        this.jwtConfig = jwtConfig;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user){
        return Jwts.builder()
                .subject(user.getUserName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getJwtExpiration()))
                .signWith(jwtConfig.getJwtSecret())
                .compact();
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);

    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(jwtConfig.getJwtSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
