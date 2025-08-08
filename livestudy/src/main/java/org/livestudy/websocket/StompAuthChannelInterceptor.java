package org.livestudy.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;




@Component
@RequiredArgsConstructor
@Slf4j
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            log.warn("STOMP accessor is null.");
            return message;
        }
        StompCommand command = accessor.getCommand();
        log.info("STOMP command received: {}", command);

        // 전체 헤더 로그 출력
        log.debug("[preSend] Native headers: {}", accessor.toNativeHeaderMap());

        if (StompCommand.CONNECT.equals(command)) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null) {
                log.warn("Authorization header is missing in STOMP CONNECT");
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Authorization header does not start with Bearer: {}", authHeader);
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            log.info("JWT token extracted: {}", token);

            try {
                boolean valid = jwtTokenProvider.validateToken(token);
                log.info("[preSend] Token validation result: {}", valid);

                if (!valid) {
                    log.warn("[preSend] Invalid JWT token.");
                    throw new CustomException(ErrorCode.FORBIDDEN);
                }

                Authentication auth = jwtTokenProvider.getAuthentication(token);
                log.info("[preSend] Authenticated user: {}", auth.getName());

                accessor.setUser(auth); // STOMP 연결에 Principal 부여
            } catch (Exception e) {
                log.error("[preSend] Token processing failed: {}", e.getMessage(), e);
                throw new CustomException(ErrorCode.FORBIDDEN);
            }
        } else {
            log.debug("STOMP command {} is not CONNECT, skipping authentication", command);
        }

        return message;
    }

}
