package org.livestudy.websocket;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.livestudy.service.livekit.LiveKitTokenService;
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

    private final LiveKitTokenService liveKitTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            log.warn("STOMP accessor is null. Message: {}", message);
            return message;
        }

        StompCommand command = accessor.getCommand();
        log.info("[StompAuthChannelInterceptor] STOMP command received: {}", command);
        log.debug("[StompAuthChannelInterceptor] All headers: {}", accessor.toMessageHeaders());
        log.debug("[StompAuthChannelInterceptor] Native headers: {}", accessor.toNativeHeaderMap());

        if (StompCommand.CONNECT.equals(command)) {
            log.info("[CONNECT] Start authentication process");

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.debug("[CONNECT] Raw Authorization header: {}", authHeader);

            if (authHeader == null) {
                log.warn("[CONNECT] Authorization header is missing");
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("[CONNECT] Authorization header does not start with Bearer: {}", authHeader);
                throw new CustomException(ErrorCode.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);
            log.info("[CONNECT] JWT token extracted: {}", token);

            try {
                boolean valid = false;
                Authentication auth = null;

                try{
                    valid = liveKitTokenService.validateToken(token);
                    if(valid) {
                        auth = liveKitTokenService.getAuthentication(token);
                        log.info("Successfully authenticated with LiveKit Token");
                    }
                } catch (Exception e) {
                    log.debug("LiveKit token validation failed, attempting to validate as general JWT");
                }

                if(auth == null) {
                    valid = jwtTokenProvider.validateToken(token);
                    if(valid) {
                        auth = jwtTokenProvider.getAuthentication(token);
                        log.info("Successfully authenticated with general JWT token.");
                    }
                }

                if(!valid){
                    log.warn("[preSend] Invalid token. Neither a valid LiveKit nor a general JWT Token");
                    throw new CustomException(ErrorCode.FORBIDDEN);
                }

                log.info("[preSend] Authenticated user : {}", auth.getName());
                accessor.setUser(auth);


            } catch (Exception e) {
                log.error("[CONNECT] Token processing failed: {}", e.getMessage(), e);
                throw new CustomException(ErrorCode.FORBIDDEN);
            }

        } else {
            log.debug("[{}] Skipping authentication, current user in accessor: {}", command, accessor.getUser());
        }

        return message;
    }

}
