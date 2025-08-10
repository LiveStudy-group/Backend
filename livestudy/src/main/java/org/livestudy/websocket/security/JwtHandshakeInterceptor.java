package org.livestudy.websocket.security;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.service.livekit.LiveKitTokenService;
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


    private final LiveKitTokenService liveKitTokenService;
    private final LiveKitTokenParser liveKitTokenParser;

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
            log.warn("토큰이 존재하지 않아 WS 연결 거부 (401 Unauthorized)");
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            // LiveKit 토큰 검증 시도
            log.info("LiveKit 토큰 검증 시도 중...");
            if (liveKitTokenService.validateToken(token)) {
                log.info("LiveKit 토큰 유효함. 토큰에서 userId, roomId 추출 중...");
                String userId = liveKitTokenParser.extractUserId(token);
                String roomId = liveKitTokenParser.extractRoomId(token);

                if (userId == null || roomId == null) {
                    log.warn("LiveKit 토큰에서 userId 또는 roomId 추출 실패");
                    httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return false;
                }

                log.info("LiveKit 토큰에서 추출된 userId={}, roomId={}", userId, roomId);
                attributes.put("userId", userId);
                attributes.put("roomId", roomId);

                return true;
            } else{
                log.warn("Livekit Token이 아닙니다.");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT 파싱/검증 오류: {}", e.getMessage());
            httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        } catch (CustomException e) {
            log.warn("CustomException 발생: {}", e.getErrorCode());
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
            log.error("WS 핸드쉐이크 중 알 수 없는 오류 발생: {}", e.getMessage(), e);
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
