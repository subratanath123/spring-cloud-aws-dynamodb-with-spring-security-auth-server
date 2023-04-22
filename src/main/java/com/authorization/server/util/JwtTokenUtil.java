package com.authorization.server.util;

import com.authorization.server.dao.AuthUserDao;
import com.authorization.server.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.authorization.server.util.Util.getRequest;

@Service
public class JwtTokenUtil {

    private static final KeyPair keyPair;

    static {
        try {
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

//
//    static {
//        try {
//            privateKey = new String(Files.readAllBytes(Paths.get(JwtTokenUtil.class.getResource("/key/private_key.pem").toURI())), Charset.defaultCharset());
//            publicKey = new String(Files.readAllBytes(Paths.get(JwtTokenUtil.class.getResource("/key/public_key.pem").toURI())), Charset.defaultCharset());
//        } catch (IOException | URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Autowired
    private AuthUserDao authUserDao;

    public JwtTokenUtil() throws NoSuchAlgorithmException {
    }

    public String extractEmail() {
        return extractClaim(getToken(), Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "User"); //have to assign role

        return createToken(claims, user.getEmail());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.RS512, keyPair.getPrivate())
                .compact();
    }

    public boolean validateToken() {
        String token = getToken();

        if (token == null) {
            return false;
        }

        String email = extractEmail();

        UserDetails userDetails = authUserDao.getUserDetails(email);

        if (userDetails == null) {
            return false;
        }

        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String getToken() {
        String authorizationHeader = getRequest().getHeader("Authorization");

        if (authorizationHeader == null || authorizationHeader.startsWith("Bearer")) {
            return null;
        }

        return authorizationHeader.split(" ")[1].trim();
    }
}