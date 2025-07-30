package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자가 스터디룸에서 퇴장할 때 전송되는 페이로드
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLeavePayload {

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
     * 퇴장 인사 메시지 (선택사항)
     * 예: "수고하셨습니다!", "다음에 또 만나요!"
     */
    private String message;

    /**
     * 이번 세션에서의 총 집중 시간 (초 단위)
     */
    private Integer totalStudyTime;

    /**
     * 이번 세션에서의 총 자리비움 시간 (초 단위)
     */
    private Integer totalAwayTime;

    /**
     * 퇴장 사유 (선택사항)
     * 예: "NORMAL" (정상 퇴장), "DISCONNECTED" (연결 끊김), "FORCED" (강제 퇴장)
     */
    private String leaveReason;

    /**
     * 방에 머무른 총 시간 (초 단위)
     */
    private Integer totalSessionTime;

    /**
     * 퇴장 시간 (밀리초 타임스탬프)
     */
    private Long timestamp;

    /**
     * 임시 퇴장 여부 (true면 곧 다시 돌아올 예정)
     */
    private Boolean isTemporary;

    /**
     * 기본 사용자 퇴장 페이로드 생성
     */
    public static UserLeavePayload of(String userId, String nickname) {
        return UserLeavePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .timestamp(System.currentTimeMillis())
                .leaveReason("NORMAL")
                .isTemporary(false)
                .build();
    }

    /**
     * 메시지와 함께 사용자 퇴장 페이로드 생성
     */
    public static UserLeavePayload of(String userId, String nickname, String message) {
        return UserLeavePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .leaveReason("NORMAL")
                .isTemporary(false)
                .build();
    }

    /**
     * 시간 정보와 함께 사용자 퇴장 페이로드 생성
     */
    public static UserLeavePayload of(String userId, String nickname, String message,
                                      Integer totalStudyTime, Integer totalAwayTime) {
        return UserLeavePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .totalStudyTime(totalStudyTime)
                .totalAwayTime(totalAwayTime)
                .timestamp(System.currentTimeMillis())
                .leaveReason("NORMAL")
                .isTemporary(false)
                .build();
    }

    /**
     * 완전한 사용자 퇴장 페이로드 생성
     */
    public static UserLeavePayload of(String userId, String nickname, String message,
                                      Integer totalStudyTime, Integer totalAwayTime,
                                      String leaveReason) {
        return UserLeavePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .totalStudyTime(totalStudyTime)
                .totalAwayTime(totalAwayTime)
                .leaveReason(leaveReason)
                .timestamp(System.currentTimeMillis())
                .isTemporary(false)
                .build();
    }

    /**
     * 연결 끊김으로 인한 퇴장 페이로드 생성
     */
    public static UserLeavePayload disconnected(String userId, String nickname) {
        return UserLeavePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(nickname + "님의 연결이 끊어졌습니다.")
                .leaveReason("DISCONNECTED")
                .timestamp(System.currentTimeMillis())
                .isTemporary(true)
                .build();
    }

    /**
     * 임시 퇴장 페이로드 생성
     */
    public static UserLeavePayload temporary(String userId, String nickname, String message) {
        return UserLeavePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .leaveReason("TEMPORARY")
                .timestamp(System.currentTimeMillis())
                .isTemporary(true)
                .build();
    }
}