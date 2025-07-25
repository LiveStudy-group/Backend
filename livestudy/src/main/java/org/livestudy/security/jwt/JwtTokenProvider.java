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

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    private final long tokenvalidtime = 60 * 60 * 1000;

    @PostConstruct
    public void init(){this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
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
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            Jwt<?, ?> jwt = Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parse(token); // parseSignedClaims 도 사용 가능

            Claims claims = (Claims) jwt.getPayload();

            String userId = claims.getSubject(); // 필요 시 사용

            return !isTokenExpired(claims); // 수정된 부분
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }

    // Claims 내용 반환
    private Claims parseClaims(String token) {
        Jwt<?, ?> jwt = Jwts
                .parser()               // parserBuilder() → parser()
                .verifyWith(key)       // setSigningKey(...) → verifyWith(key)
                .build()
                .parse(token);         // parseClaimsJws(...) → parse(...)

        return (Claims) jwt.getPayload();  // getBody() → getPayload()
    }

    // Token으로 이메일 가져오기
    public String getEmailFromToken(String token) {

        return parseClaims(token).getSubject();
    }
    // 토큰 만료 여부 체크
    private boolean isTokenExpired(Claims claims){
            return claims.getExpiration().before(new Date());
        }

    // 사용자 고유 식별자(ID)를 추출하기 위한 Method
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);

    }
}
