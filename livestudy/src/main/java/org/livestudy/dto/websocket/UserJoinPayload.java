package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자가 스터디룸에 입장할 때 전송되는 페이로드
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinPayload {

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
     * 입장 인사 메시지 (선택사항)
     * 예: "안녕하세요!", "열심히 공부해요!"
     */
    private String message;

    /**
     * 사용자 레벨 또는 등급 (선택사항)
     */
    private String userLevel;

    /**
     * 입장 시간 (밀리초 타임스탬프)
     */
    private Long timestamp;

    /**
     * 이전 세션 정보가 있는지 여부
     * true면 이전에 참여했던 방에 재입장하는 것
     */
    private Boolean isRejoining;

    /**
     * 기본 사용자 입장 페이로드 생성
     */
    public static UserJoinPayload of(String userId, String nickname) {
        return UserJoinPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .timestamp(System.currentTimeMillis())
                .isRejoining(false)
                .build();
    }

    /**
     * 메시지와 함께 사용자 입장 페이로드 생성
     */
    public static UserJoinPayload of(String userId, String nickname, String message) {
        return UserJoinPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .isRejoining(false)
                .build();
    }

    /**
     * 완전한 사용자 입장 페이로드 생성
     */
    public static UserJoinPayload of(String userId, String nickname, String profileImage,
                                     String message, Boolean isRejoining) {
        return UserJoinPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .profileImage(profileImage)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .isRejoining(isRejoining)
                .build();
    }

    /**
     * 재입장 페이로드 생성
     */
    public static UserJoinPayload rejoining(String userId, String nickname) {
        return UserJoinPayload.builder()
                .userId(userId)
                .nickname(nickname)
                .timestamp(System.currentTimeMillis())
                .isRejoining(true)
                .build();
    }
}