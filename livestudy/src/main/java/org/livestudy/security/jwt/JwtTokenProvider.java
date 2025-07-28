package org.livestudy.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.livestudy.domain.user.User;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    private final long tokenValidTime = 60 * 60 * 1000;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String generateToken(Authentication authentication) {
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + tokenValidTime);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getUser().getId())
                .issuedAt(now)
                .expiration(expireDate)
                .signWith(key)
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


    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwt<?, ?> jwt = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parse(token);

            Claims claims = (Claims) jwt.getPayload();

            return !isTokenExpired(claims);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }


    private Claims parseClaims(String token) {
        Jwt<?, ?> jwt = Jwts.parser()
                .verifyWith(key)
                .build()
                .parse(token);

        return (Claims) jwt.getPayload();
    }

    // 이메일 추출
    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 사용자 ID 추출
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    // 만료 여부
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    // 토큰 만료 처리
    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }
}
