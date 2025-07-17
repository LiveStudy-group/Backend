package org.livestudy.websocket;

import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    public StompAuthChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(StompCommand.CONNECT.equals(accessor.getCommand())) { // Connect 메시지인지 확인
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) { // 토큰 전달 유무 확인
                token = token.substring(7); // 앞에 "Bearer "를 제거하는 역할이다.
                if(jwtTokenProvider.validateToken(token)) { // 실제 JWT 문자열 추출
                    String userId = jwtTokenProvider.getUserId(token);
                    accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
                } else {
                    throw new IllegalArgumentException("Invalid token");
                }
            } else {
                throw new IllegalArgumentException("No Token found");
            }
        }

        return message;
    }
}
