package org.livestudy.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.livestudy.domain.user.User;
import org.livestudy.security.SecurityUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private JwtParser jwtParser;

    private final long tokenvalidtime = 60 * 60 * 1000;


    @PostConstruct
    protected void init(){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith((SecretKey) key).build();
    }

    // token 생성
    public String generateToken(Authentication authentication) {
        SecurityUser user =  (SecurityUser) authentication.getPrincipal();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + tokenvalidtime);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getUser().getId())
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 인증 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        SecurityUser principal = new SecurityUser(
                User.builder()
                        .id(userId)
                        .email(email)
                        .build()
        );

        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    // token 검증
    public boolean validateToken(String token){
        try{
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    // Claims 내용 반환
    private Claims parseClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    // Token으로 이메일 가져오기
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

}