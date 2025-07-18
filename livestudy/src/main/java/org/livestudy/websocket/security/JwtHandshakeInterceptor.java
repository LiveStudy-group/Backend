package org.livestudy.websocket.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean beforeHandshake(ServerHttpRequest httpRequest,
                                   ServerHttpResponse httpResponse,
                                   WebSocketHandler handler,
                                   Map<String, Object> attributes) throws Exception {

        String token = UriComponentsBuilder.fromUri(httpRequest.getURI())
                .build().getQueryParams()
                .getFirst("token");

        if (token == null) {
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        } try {
            Claims c = Jwts.parser()
                    .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            attributes.put("userId", c.getSubject());
            attributes.put("roomId", c.get("room", String.class));
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest httpRequest,
                               ServerHttpResponse httpResponse,
                               WebSocketHandler handler,
                               @Nullable Exception ex) {

        String ip   = httpRequest.getRemoteAddress() != null ? httpRequest.getRemoteAddress().toString() : "unknown";
        String path = httpRequest.getURI().getPath();

        if (ex == null) {
            log.info("✅ WS Handshake SUCCESS  {} from {}", path, ip);
        } else {
            log.warn("❌ WS Handshake FAILED   {} from {} – {}", path, ip, ex.getMessage());
        }
    }
}
