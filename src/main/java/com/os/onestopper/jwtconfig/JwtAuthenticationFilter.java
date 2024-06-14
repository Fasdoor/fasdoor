package com.os.onestopper.jwtconfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private String SECRET_KEY; // Change this to your secret key

    public JwtAuthenticationFilter(@Value("${jwt.secretKey}") String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    private final List<String> unsecuredUrls = Arrays.asList("/address", "/contact");
    private String encodedKey;// URLs to leave unsecured

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.contains("/login") || requestURI.contains("/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isUnsecuredUrl(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        String token = header.substring(7);
        try {
            encodedKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
            Claims claims = Jwts.parser().setSigningKey(encodedKey).build().parseSignedClaims(token).getBody();
            request.setAttribute("claims", claims);
        } catch (SignatureException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isUnsecuredUrl(String requestURI) {
        return unsecuredUrls.stream().anyMatch(requestURI::endsWith);
    }
}
