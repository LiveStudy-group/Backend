package org.livestudy.websocket.security;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.redis.RoomRedisRepository;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.livestudy.service.StudyRoomService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    private final StudyRoomService studyRoomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoomRedisRepository roomRedisRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest httpRequest,
                                   ServerHttpResponse httpResponse,
                                   WebSocketHandler handler,
                                   Map<String, Object> attributes) throws Exception {

        String requestPath = httpRequest.getURI().getPath();
        String ip = httpRequest.getRemoteAddress() != null ? httpRequest.getRemoteAddress().toString() : "unknown";

        String token = UriComponentsBuilder.fromUri(httpRequest.getURI())
                .build().getQueryParams()
                .getFirst("access_token");





        log.info("🛡️ WS Handshake 요청: path={}, ip={}, token={}", requestPath, ip, token != null ? "present" : "missing");

        if (token == null) {
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        } try {
            String userId = jwtTokenProvider.getUserIdFromToken(token).toString();
            attributes.put("userId", userId);

            // 입장할 방
            String roomId = studyRoomService.enterRoom(userId).toString();
            attributes.put("roomId", roomId);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.USER_ALREADY_IN_ROOM) {
                httpResponse.setStatusCode(HttpStatus.CONFLICT);
            } else if (e.getErrorCode() == ErrorCode.USER_NOT_IN_ROOM ||
                    e.getErrorCode() == ErrorCode.INVALID_ROOM_CAPACITY) {
                httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
            } else if (e.getErrorCode() == ErrorCode.REDIS_CONNECTION_FAILED){
                httpResponse.setStatusCode(HttpStatus.PRECONDITION_FAILED);
            } else {
                httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return false;
        } catch (Exception e) {
            httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
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
