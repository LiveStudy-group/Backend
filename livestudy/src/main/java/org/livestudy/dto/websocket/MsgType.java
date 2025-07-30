package org.livestudy.dto.websocket;

/**
 * WebSocket 메시지 타입을 정의하는 열거형
 * 클라이언트와 서버 간의 메시지 타입을 구분하는 데 사용됩니다.
 */
public enum MsgType {

    // === 채팅 관련 ===
    /**
     * 일반 채팅 메시지
     */
    CHAT_MESSAGE,

    /**
     * 채팅 이미지 메시지
     */
    CHAT_IMAGE,

    /**
     * 채팅 파일 메시지
     */
    CHAT_FILE,

    /**
     * 채팅 이모지 메시지
     */
    CHAT_EMOJI,

    // === 집중 타이머 관련 ===
    /**
     * 집중 시작
     */
    FOCUS_START,

    /**
     * 집중 일시정지 (자리비움)
     */
    FOCUS_PAUSE,

    /**
     * 집중 재개
     */
    FOCUS_RESUME,

    /**
     * 집중 종료
     */
    FOCUS_STOP,

    /**
     * 집중 종료 (기존 호환성 유지)
     */
    FOCUS_END,

    // === 타이머 성공 응답 ===
    /**
     * 집중 시작 성공
     */
    FOCUS_START_SUCCESS,

    /**
     * 집중 일시정지 성공
     */
    FOCUS_PAUSE_SUCCESS,

    /**
     * 집중 재개 성공
     */
    FOCUS_RESUME_SUCCESS,

    /**
     * 집중 종료 성공
     */
    FOCUS_STOP_SUCCESS,

    // === 사용자 상태 관련 ===
    /**
     * 사용자 방 입장
     */
    USER_JOIN,

    /**
     * 사용자 방 퇴장
     */
    USER_LEAVE,

    /**
     * 사용자 상태 업데이트 (집중/자리비움 상태 변경)
     */
    USER_STATUS_UPDATE,

    // === 시스템 메시지 ===
    /**
     * 시스템 알림 메시지
     */
    SYSTEM_MESSAGE,

    /**
     * 정보 메시지
     */
    INFO_MESSAGE,

    /**
     * 경고 메시지
     */
    WARNING_MESSAGE,

    /**
     * 에러 메시지
     */
    ERROR_MESSAGE,

    /**
     * 에러 (간소화)
     */
    ERROR,

    /**
     * 성공 응답
     */
    SUCCESS,

    // === 방 관리 관련 ===
    /**
     * 방 정보 업데이트
     */
    ROOM_UPDATE,

    /**
     * 참여자 목록 업데이트
     */
    PARTICIPANTS_UPDATE,

    /**
     * 방 설정 변경
     */
    ROOM_SETTINGS_UPDATE,

    // === 연결 관리 ===
    /**
     * 연결 확인 (Ping)
     */
    PING,

    /**
     * 연결 응답 (Pong)
     */
    PONG,

    /**
     * 연결 끊김 알림
     */
    DISCONNECT,

    /**
     * 재연결 요청
     */
    RECONNECT,

    // === 알림 관련 ===
    /**
     * 일반 알림
     */
    NOTIFICATION,

    /**
     * 긴급 알림
     */
    URGENT_NOTIFICATION,

    // === 미래 확장용 ===
    /**
     * 화면 공유 시작
     */
    SCREEN_SHARE_START,

    /**
     * 화면 공유 종료
     */
    SCREEN_SHARE_END,

    /**
     * 음성 채팅 시작
     */
    VOICE_CHAT_START,

    /**
     * 음성 채팅 종료
     */
    VOICE_CHAT_END,

    /**
     * 비디오 채팅 시작
     */
    VIDEO_CHAT_START,

    /**
     * 비디오 채팅 종료
     */
    VIDEO_CHAT_END;

    /**
     * 채팅 관련 메시지 타입인지 확인
     */
    public boolean isChatType() {
        return this == CHAT_MESSAGE ||
                this == CHAT_IMAGE ||
                this == CHAT_FILE ||
                this == CHAT_EMOJI;
    }

    /**
     * 타이머 관련 메시지 타입인지 확인
     */
    public boolean isTimerType() {
        return this == FOCUS_START ||
                this == FOCUS_PAUSE ||
                this == FOCUS_RESUME ||
                this == FOCUS_STOP ||
                this == FOCUS_END;
    }

    /**
     * 사용자 상태 관련 메시지 타입인지 확인
     */
    public boolean isUserStatusType() {
        return this == USER_JOIN ||
                this == USER_LEAVE ||
                this == USER_STATUS_UPDATE;
    }

    /**
     * 시스템 메시지 타입인지 확인
     */
    public boolean isSystemType() {
        return this == SYSTEM_MESSAGE ||
                this == INFO_MESSAGE ||
                this == WARNING_MESSAGE ||
                this == ERROR_MESSAGE ||
                this == ERROR ||
                this == SUCCESS;
    }

    /**
     * 성공 응답 타입인지 확인
     */
    public boolean isSuccessType() {
        return this == SUCCESS ||
                this == FOCUS_START_SUCCESS ||
                this == FOCUS_PAUSE_SUCCESS ||
                this == FOCUS_RESUME_SUCCESS ||
                this == FOCUS_STOP_SUCCESS;
    }

    /**
     * 에러 관련 타입인지 확인
     */
    public boolean isErrorType() {
        return this == ERROR ||
                this == ERROR_MESSAGE;
    }

    /**
     * 연결 관리 관련 타입인지 확인
     */
    public boolean isConnectionType() {
        return this == PING ||
                this == PONG ||
                this == DISCONNECT ||
                this == RECONNECT;
    }
}