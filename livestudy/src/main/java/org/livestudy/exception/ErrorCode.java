package org.livestudy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통 에러
    INTERNAL_SERVER_ERROR("C001", "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("C002", "잘못된 입력 값입니다.", HttpStatus.BAD_REQUEST),

    // 유저 관련 에러
    USER_NOT_FOUND("U001", "존재하지 않는 사용자입니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("U002", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("U003", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    ROOM_NOT_FOUND("U004", "존재하지 않는 방입니다.", HttpStatus.NOT_FOUND),
    NO_ROOMS_IN_SERVER("U005", "방이 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_IN_ROOM("U006", "이미 방에 들어가 있는 상황입니다.", HttpStatus.CONFLICT),
    INVALID_ROOM_CAPACITY("U007", "방의 정원이 알맞지 않습니다.", HttpStatus.BAD_REQUEST),
    ROOM_IS_FULL("U008", "방이 이미 가득 찼습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_ROOM("U009", "사용자가 방에 없습니다.", HttpStatus.BAD_REQUEST),
    USER_BLOCKED("U010", "정지된 사용자입니다.", HttpStatus.FORBIDDEN),
    USER_WITHDRAW("U011", "탈퇴한 사용자입니다.", HttpStatus.FORBIDDEN),


    // Track 관련 에러
    TRACK_TYPE_SHOULD_NOT_BE_NULL("T001", "Track의 Type은 Null일 수 없습니다.", HttpStatus.BAD_REQUEST),
    TRACK_SID_SHOULD_NOT_BE_NULL("T002", "TrackSid는 Null일 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 인증 관련 에러
    UNAUTHORIZED("A001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("A002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN("A003", "토큰이 만료되었습니다.", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS),
    REDIS_CONNECTION_FAILED("A004", "Redis 서버가 끊겼습니다.", HttpStatus.PRECONDITION_FAILED),

    // WebSocket 관련 에러
    USER_ID_MISMATCH("W001", "userId가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
    USER_SUSPENDED("W002", "정지된 이용자입니다.", HttpStatus.FORBIDDEN),
    BAD_SCHEMA("W003", "잘못된 메시지 형식입니다.", HttpStatus.BAD_REQUEST),
    WEBSOCKET_CONNECTION_ERROR("W004", "WebSocket 연결에 실패했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    WEBSOCKET_SESSION_EXPIRED("W005", "WebSocket 세션이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    WEBSOCKET_INVALID_DESTINATION("W006", "잘못된 WebSocket 경로입니다.", HttpStatus.BAD_REQUEST),
    WEBSOCKET_MESSAGE_ERROR("W007", "WebSocket 메시지 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WEBSOCKET_AUTHORIZATION_FAILED("W008", "WebSocket 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),

    // 신고 관련 에러
    DUPLICATE_REPORT("R001", "이미 동일한 사유로 신고했습니다.", HttpStatus.CONFLICT),
    CANNOT_REPORT_SELF("R002", "자신을 신고할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 타이머 관련 에러
    TIMER_NOT_FOUND("T001", "타이머 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TIMER_ALREADY_RUNNING("T002", "이미 타이머가 실행 중입니다.", HttpStatus.CONFLICT),
    TIMER_NOT_RUNNING("T003", "실행 중인 타이머가 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_TIMER_STATE("T004", "잘못된 타이머 상태입니다.", HttpStatus.BAD_REQUEST),
    TIMER_SERVICE_ERROR("T005", "타이머 서비스 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 스터디룸 관련 에러
    STUDY_ROOM_NOT_FOUND("S001", "존재하지 않는 스터디룸입니다.", HttpStatus.NOT_FOUND),
    PARTICIPANT_NOT_FOUND("S002", "참여자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_ROOM_PARTICIPANT("S003", "해당 방의 참여자가 아닙니다.", HttpStatus.FORBIDDEN),
    ROOM_ACCESS_DENIED("S004", "방 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    ROOM_CAPACITY_EXCEEDED("S005", "방 최대 인원을 초과했습니다.", HttpStatus.BAD_REQUEST),

    // 채팅 관련 에러
    CHAT_MESSAGE_TOO_LONG("CH001", "메시지가 너무 깁니다.", HttpStatus.BAD_REQUEST),
    CHAT_MESSAGE_EMPTY("CH002", "빈 메시지는 전송할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHAT_PERMISSION_DENIED("CH003", "채팅 권한이 없습니다.", HttpStatus.FORBIDDEN),
    CHAT_RATE_LIMIT_EXCEEDED("CH004", "채팅 전송 한도를 초과했습니다.", HttpStatus.TOO_MANY_REQUESTS),
    INVALID_MESSAGE_TYPE("CH005", "지원하지 않는 메시지 타입입니다.", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED("CH006", "파일 업로드에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_SIZE_EXCEEDED("CH007", "파일 크기가 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_FILE_TYPE("CH008", "지원하지 않는 파일 형식입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}