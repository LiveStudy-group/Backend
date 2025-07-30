package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 채팅 메시지 전송 시 사용되는 페이로드
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagePayload {

    /**
     * 메시지 고유 ID (선택사항)
     */
    private String messageId;

    /**
     * 발신자 사용자 ID
     */
    private String userId;

    /**
     * 발신자 닉네임
     */
    private String nickname;

    /**
     * 발신자 프로필 이미지 URL (선택사항)
     */
    private String profileImage;

    /**
     * 실제 메시지 내용
     */
    private String message;

    /**
     * 메시지 타입
     * - TEXT: 일반 텍스트
     * - IMAGE: 이미지
     * - FILE: 파일
     * - EMOJI: 이모지
     * - SYSTEM: 시스템 메시지
     */
    private String messageType;

    /**
     * 첨부 파일 URL (이미지, 파일 전송 시)
     */
    private String attachmentUrl;

    /**
     * 첨부 파일명 (파일 전송 시)
     */
    private String attachmentName;

    /**
     * 첨부 파일 크기 (바이트 단위)
     */
    private Long attachmentSize;

    /**
     * 답장하는 메시지 ID (답장 기능 사용 시)
     */
    private String replyToMessageId;

    /**
     * 메시지 전송 시간 (밀리초 타임스탬프)
     */
    private Long timestamp;

    /**
     * 메시지 읽음 여부 표시용 사용자 ID 목록 (선택사항)
     */
    private List<String> readByUsers;

    /**
     * 기본 텍스트 메시지 페이로드 생성
     */
    public static ChatMessagePayload of(String userId, String nickname, String message) {
        return ChatMessagePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .messageType("TEXT")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 프로필 이미지와 함께 텍스트 메시지 페이로드 생성
     */
    public static ChatMessagePayload of(String userId, String nickname, String profileImage, String message) {
        return ChatMessagePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .profileImage(profileImage)
                .message(message)
                .messageType("TEXT")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 이미지 메시지 페이로드 생성
     */
    public static ChatMessagePayload imageMessage(String userId, String nickname,
                                                  String message, String imageUrl) {
        return ChatMessagePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .messageType("IMAGE")
                .attachmentUrl(imageUrl)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 파일 메시지 페이로드 생성
     */
    public static ChatMessagePayload fileMessage(String userId, String nickname,
                                                 String message, String fileUrl,
                                                 String fileName, Long fileSize) {
        return ChatMessagePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .messageType("FILE")
                .attachmentUrl(fileUrl)
                .attachmentName(fileName)
                .attachmentSize(fileSize)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 답장 메시지 페이로드 생성
     */
    public static ChatMessagePayload replyMessage(String userId, String nickname,
                                                  String message, String replyToMessageId) {
        return ChatMessagePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(message)
                .messageType("TEXT")
                .replyToMessageId(replyToMessageId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 시스템 메시지 페이로드 생성
     */
    public static ChatMessagePayload systemMessage(String message) {
        return ChatMessagePayload.builder()
                .messageType("SYSTEM")
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 이모지 메시지 페이로드 생성
     */
    public static ChatMessagePayload emojiMessage(String userId, String nickname, String emoji) {
        return ChatMessagePayload.builder()
                .userId(userId)
                .nickname(nickname)
                .message(emoji)
                .messageType("EMOJI")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}