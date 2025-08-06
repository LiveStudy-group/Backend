package org.livestudy.websocket.security;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.exception.CustomException;
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

    @Override
    public boolean beforeHandshake(ServerHttpRequest httpRequest,
                                   ServerHttpResponse httpResponse,
                                   WebSocketHandler handler,
                                   Map<String, Object> attributes) throws Exception {

        String requestPath = httpRequest.getURI().getPath();
        String ip = httpRequest.getRemoteAddress() != null ? httpRequest.getRemoteAddress().toString() : "unknown";

        String token = UriComponentsBuilder.fromUri(httpRequest.getURI())
                .build().getQueryParams()
                .getFirst("token");

        log.info("🛡️ WS Handshake 요청: path={}, ip={}, token={}", requestPath, ip, token != null ? "present" : "missing");

        if (token == null) {
            log.warn("❌ WS Handshake 실패 (토큰 없음): path={}, ip={}", requestPath, ip);
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            String userId = jwtTokenProvider.getUserIdFromToken(token).toString();
            log.info("✅ 토큰 검증 성공: userId={}", userId);
            attributes.put("userId", userId);

            String roomId = studyRoomService.enterRoom(userId).toString();
            log.info("🚪 방 입장 성공: userId={}, roomId={}", userId, roomId);
            attributes.put("roomId", roomId);

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ 토큰 검증 실패: token={}, error={}", token, e.getMessage());
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;

        } catch (CustomException e) {
            log.warn("❌ 입장 처리 실패: errorCode={}, message={}", e.getErrorCode(), e.getMessage());

            switch (e.getErrorCode()) {
                case USER_ALREADY_IN_ROOM:
                    httpResponse.setStatusCode(HttpStatus.CONFLICT);
                    break;
                case USER_NOT_IN_ROOM:
                case INVALID_ROOM_CAPACITY:
                    httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
                    break;
                case REDIS_CONNECTION_FAILED:
                    httpResponse.setStatusCode(HttpStatus.PRECONDITION_FAILED);
                    break;
                default:
                    httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return false;

        } catch (Exception e) {
            log.error("❌ 알 수 없는 예외 발생: {}", e.getMessage(), e);
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
