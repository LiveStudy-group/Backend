package org.livestudy.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor stompAuthChannelInterceptor;

    public WebSocketConfiguration(StompAuthChannelInterceptor stompAuthChannelInterceptor) {
        this.stompAuthChannelInterceptor = stompAuthChannelInterceptor;
    }

    @Override // Client가 처음으로 WebSocket을 연결하는 Endpoint를 설정
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
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
