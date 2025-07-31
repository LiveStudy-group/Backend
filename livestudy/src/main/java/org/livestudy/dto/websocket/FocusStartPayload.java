package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 집중 시작/재개 시 전송되는 페이로드
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FocusStartPayload {

    /**
     * 사용자 ID
     */
    private String userId;

    /**
     * 사용자 닉네임
     */
    private String nickname;

    /**
     * 사용자 프로필 이미지 URL (선택사항)
     */
    private String profileImage;

    /**
     * 집중 시작 메시지 (선택사항)
     * 예: "집중을 시작합니다!", "다시 집중해보겠습니다!"
     */
    private String message;

    /**
     * 집중 목표 시간 (분 단위, 선택사항)
     */
    private Integer targetMinutes;

    /**
     * 집중 시작 시간 (밀리초 타임스탬프)
     */
    private Long timestamp;

    /**
     * 기본 집중 시작 페이로드 생성
     */
    public static FocusStartPayload of(String userId, String nickname) {
        return FocusStartPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 메시지와 함께 집중 시작 페이로드 생성
     */
    public static FocusStartPayload of(String userId, String nickname, String message) {
        return FocusStartPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 목표 시간과 함께 집중 시작 페이로드 생성
     */
    public static FocusStartPayload of(String userId, String nickname, String message, Integer targetMinutes) {
        return FocusStartPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .targetMinutes(targetMinutes)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}