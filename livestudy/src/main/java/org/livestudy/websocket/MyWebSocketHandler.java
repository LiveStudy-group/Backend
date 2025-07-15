package org.livestudy.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    // 클라이언트가 메시지를 보냈을 때 호출
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("📩 수신 메시지: " + payload);

        // 메시지 그대로 돌려주기 (Echo)
        session.sendMessage(new TextMessage("Echo: " + payload));
    }

    // 클라이언트 연결이 열렸을 때 호출
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("🔌 클라이언트 연결됨: " + session.getId());
    }

    private boolean isNormalClosure(CloseStatus status) {
        return status.getCode() == 1000;
    }

    // 클라이언트 연결이 닫혔을 때 호출
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (isNormalClosure(status)) {
            System.out.println("✅ 정상 종료: " + session.getId());
        } else {
            System.out.println("⚠️ 비정상 종료: " + session.getId() + ", 코드: " + status.getCode() + ", 이유: " + status.getReason());
        }
        // 여기서 연결 끊긴 세션 정리 작업 가능
    }

    // 전송 중 에러 발생 시 호출
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("❌ 연결 오류 발생: " + session.getId() + ", 오류: " + exception.getMessage());
        // 필요 시 연결 강제 종료
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }


}
