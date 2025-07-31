package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * WebSocket 메시지의 기본 구조를 정의하는 래퍼 클래스
 * 모든 WebSocket 메시지는 이 형태로 전송됩니다.
 *
 * @param <T> 실제 페이로드 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseMsg<T> {

    /**
     * 메시지 타입 (FOCUS_START, CHAT_MESSAGE 등)
     */
    private MsgType type;

    /**
     * 실제 메시지 내용
     */
    private T payload;

    /**
     * 메시지 전송 시간 (밀리초 타임스탬프)
     */
    private Long timestamp;

    /**
     * 편의 메서드: 현재 시간으로 타임스탬프를 설정하여 생성
     */
    public static <T> BaseMsg<T> create(MsgType type, T payload) {
        return BaseMsg.<T>builder()
                .type(type)
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}