//package com.os.onestopper.jwtconfig;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Base64;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class AuthenticationService {
//    @Value("${jwt.secretKey}")
//    private String SECRET_KEY;
//    @Value("${jwt.expiration}")
//    private long jwtExpiration;
//    @Value("${jwt.refresh-token}")
//    private long refreshExpiration;
//
////    public AuthenticationService(@Value("${jwt.secretKey}") String SECRET_KEY) {
////        this.SECRET_KEY = SECRET_KEY;
////    }
//
//    private String encodedKey;
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public String generateToken(String userDetails) {
//        return generateToken(new HashMap<>(), userDetails);
//    }
//
//    public String generateToken(Map<String, Object> extraClaims, String userDetails) {
//        return buildToken(extraClaims, userDetails, jwtExpiration);
//    }
//
//    public String generateRefreshToken(String userDetails) {
//        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
//    }
//
//    private String buildToken(Map<String, Object> extraClaims, String userDetails, long expiration) {
//        return Jwts
//                .builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }
//
//    public boolean isTokenValid(String token) {
//        final String username = extractUsername(token);
//        return !isTokenExpired(token);
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts
//                .parser()
//                .setSigningKey(getSignInKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    private Key getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}
