package org.livestudy.websocket.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Map;

@Component
@Slf4j
public class LiveKitTokenParser {


    private final String apiSecret;

    public LiveKitTokenParser(@Value("${livekit.api-secret}")String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public Claims parseClaims(String token){
        try {
            log.info("Parsing Livekit Jwt Token...");

            // 만약 apiSecret이 그냥 평문 문자열이라면 (Base64 디코딩 빼고):
             byte[] keyBytes = apiSecret.getBytes(StandardCharsets.UTF_8);
             Key key = Keys.hmacShaKeyFor(keyBytes);

            Jws<Claims> jwsClaims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            log.info("Token parsed successfully : {}", jwsClaims.getBody().getSubject());
            return jwsClaims.getBody();
        } catch (JwtException e) {
            log.error("failed to parse and validate Livekit Jwt Token: {}", e.getMessage());
            throw e;
        }
    }

    public String extractUserId(String token){
        Claims claims = parseClaims(token);
        String userId = claims.getSubject();
        log.info("Extracted userId : {}", userId);

        return claims.getSubject();
    }

    public String extractRoomId(String token){
        Claims claims = parseClaims(token);
        Object videoObj = claims.get("video");
        if (videoObj instanceof Map<?, ?> videoMap) {
            Object roomObj = videoMap.get("room");
            if (roomObj instanceof String room) {
                log.info("extracted roomId : {}", room);
                return room;
            }
        }
        log.warn("RoomId not found in token payload");
        return null;
    }
}
