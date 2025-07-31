package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 집중 일시정지/종료 시 전송되는 페이로드
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FocusEndPayload {

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
     * 집중 종료/일시정지 메시지 (선택사항)
     * 예: "집중을 종료했습니다.", "잠시 자리를 비웁니다."
     */
    private String message;

    /**
     * 이번 세션 총 집중 시간 (초 단위)
     */
    private Integer totalStudyTime;

    /**
     * 이번 세션 총 자리비움 시간 (초 단위)
     */
    private Integer totalAwayTime;

    /**
     * 집중 종료/일시정지 사유 (선택사항)
     * 예: "BREAK", "EMERGENCY", "FINISHED" 등
     */
    private String reason;

    /**
     * 집중 종료 시간 (밀리초 타임스탬프)
     */
    private Long timestamp;

    /**
     * 기본 집중 종료 페이로드 생성
     */
    public static FocusEndPayload of(String userId, String nickname) {
        return FocusEndPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 메시지와 함께 집중 종료 페이로드 생성
     */
    public static FocusEndPayload of(String userId, String nickname, String message) {
        return FocusEndPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 완전한 집중 종료 페이로드 생성 (시간 정보 포함)
     */
    public static FocusEndPayload of(String userId, String nickname, String message,
                                     Integer totalStudyTime, Integer totalAwayTime) {
        return FocusEndPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .totalStudyTime(totalStudyTime)
                .totalAwayTime(totalAwayTime)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 사유와 함께 집중 종료 페이로드 생성
     */
    public static FocusEndPayload of(String userId, String nickname, String message,
                                     Integer totalStudyTime, Integer totalAwayTime, String reason) {
        return FocusEndPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .totalStudyTime(totalStudyTime)
                .totalAwayTime(totalAwayTime)
                .reason(reason)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}