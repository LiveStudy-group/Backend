package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livestudy.domain.studyroom.FocusStatus;

/**
 * 사용자 상태 업데이트 시 전송되는 페이로드
 * 집중/자리비움 상태 변경을 실시간으로 알립니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusPayload {

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
     * 현재 집중 상태 (FOCUS, AWAY)
     */
    private FocusStatus status;

    /**
     * 현재 세션에서의 누적 집중 시간 (초 단위)
     */
    private Integer currentStudyTime;

    /**
     * 현재 세션에서의 누적 자리비움 시간 (초 단위)
     */
    private Integer currentAwayTime;

    /**
     * 현재 상태로 변경된 시간 (밀리초 타임스탬프)
     */
    private Long statusChangedAt;

    /**
     * 상태 변경 메시지 (선택사항)
     * 예: "집중 모드로 전환", "잠시 자리를 비움"
     */
    private String message;

    /**
     * 현재 상태 지속 시간 (초 단위)
     */
    private Integer currentStatusDuration;

    /**
     * 기본 사용자 상태 페이로드 생성
     */
    public static UserStatusPayload of(String userId, String nickname, FocusStatus status) {
        return UserStatusPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .status(status)
                .statusChangedAt(System.currentTimeMillis())
                .build();
    }

    /**
     * 시간 정보와 함께 사용자 상태 페이로드 생성
     */
    public static UserStatusPayload of(String userId, String nickname, FocusStatus status,
                                       Integer currentStudyTime, Integer currentAwayTime) {
        return UserStatusPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .status(status)
                .currentStudyTime(currentStudyTime)
                .currentAwayTime(currentAwayTime)
                .statusChangedAt(System.currentTimeMillis())
                .build();
    }

    /**
     * 완전한 사용자 상태 페이로드 생성
     */
    public static UserStatusPayload of(String userId, String nickname, FocusStatus status,
                                       Integer currentStudyTime, Integer currentAwayTime,
                                       String message) {
        return UserStatusPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .status(status)
                .currentStudyTime(currentStudyTime)
                .currentAwayTime(currentAwayTime)
                .message(message)
                .statusChangedAt(System.currentTimeMillis())
                .build();
    }
}