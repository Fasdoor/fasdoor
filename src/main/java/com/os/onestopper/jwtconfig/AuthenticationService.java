package com.os.onestopper.jwtconfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Service
public class AuthenticationService {
    private String SECRET_KEY;

    public AuthenticationService(@Value("${jwt.secretKey}") String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    private String encodedKey;
    public String generateToken(String username) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + 43200000; // Token expires in 12 hour
        encodedKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(expirationTimeMillis))
                .signWith(SignatureAlgorithm.HS256, encodedKey)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            byte[] byteDecodedKey = Base64.getMimeDecoder().decode(encodedKey);
            String decodedKey = new String(byteDecodedKey, StandardCharsets.UTF_8);
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token);
            return true;
        } catch (SignatureException e) {
            // Invalid signature
            return false;
        } catch (Exception e) {
            // Other exceptions
            return false;
        }
    }
    public String getUsernameFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token);
            return claimsJws.getBody().getSubject();
        } catch (SignatureException e) {
            // Invalid signature
            return null;
        } catch (Exception e) {
            // Other exceptions
            return null;
        }
    }
}
