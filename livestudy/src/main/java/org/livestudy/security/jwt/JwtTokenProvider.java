package org.livestudy.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.livestudy.domain.user.User;
import org.livestudy.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    private final long tokenvalidtime = 60 * 60 * 1000;

    @PostConstruct
    protected void init(){this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
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
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token expired : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token malformed : {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token unsupported : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token invalid : {}", e.getMessage());
        }

        return false;
    }


    // Claims 내용 반환
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // Token으로 이메일 가져오기
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 사용자 고유 식별자(ID)를 추출하기 위한 Method
    public String getUserId(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
