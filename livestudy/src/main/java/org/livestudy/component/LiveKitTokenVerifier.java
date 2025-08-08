package org.livestudy.component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Slf4j
public class LiveKitTokenVerifier {

    private final JwtParser jwtParser;


    public LiveKitTokenVerifier(@Value("${livekit.api-secret}") String apiSecret) {
        SecretKey key = Keys.hmacShaKeyFor(apiSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
    }

    public DecodedLiveKitToken decode(String token) throws Exception {
        try {
            Jwt<?, ?> jwt = jwtParser.parse(token);
            Claims claims = (Claims) jwt.getPayload();

            String identity = claims.getSubject();  // sub 필드
            Map<String, Object> videoClaim = claims.get("video", Map.class);
            String room = videoClaim != null ? (String) videoClaim.get("room") : null;

            return new DecodedLiveKitToken(identity, room);
        } catch (JwtException e) {
            log.error("Failed to parse or validate LiveKit token: {}", token, e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            log.error("Unexpected error while decoding LiveKit token: {}", token, e);
            throw new InvalidTokenException("Error decoding LiveKit token", e);
        }
    }

    public record DecodedLiveKitToken(String identity, String roomId) {}

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
