package org.livestudy.websocket;

import org.livestudy.websocket.security.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    public WebSocketConfiguration(StompAuthChannelInterceptor stompAuthChannelInterceptor, JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.stompAuthChannelInterceptor = stompAuthChannelInterceptor;
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override // Client가 처음으로 WebSocket을 연결하는 Endpoint를 설정
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5174", "https://localhost:5174", // FE 개발용
                        "https://live-study.com", "https://www.live-study.com", "https://api.live-study.com")  // 배포용
                .addInterceptors(jwtHandshakeInterceptor) // 주소 도달 시 입장용 토큰에 대하여 인증을 진행한다!
                .withSockJS();
    }

    @Override // Message Routing 경로를 설정해서 Client가 보내는 메시지(/pub/...)와 서버가 보내는 메시지(/topic/.., /queue/...)를 구분
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub"); // Client가 보낼 주소
        registry.enableSimpleBroker("/topic", "/queue"); // 서버가 보낼 주소
    }

    @Override // Client가 보내는 STOMP 메시지를 가로채는 Interceptor를 등록한다. -> JWT 인증 & 사용자 정보 추출을 위함
    public void configureClientInboundChannel(ChannelRegistration  registration) {
        registration.interceptors(stompAuthChannelInterceptor);
    }
}
