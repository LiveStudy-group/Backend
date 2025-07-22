package org.livestudy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통 에러
    INTERNAL_SERVER_ERROR("C001", "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INPUT("C002", "잘못된 입력 값입니다.", HttpStatus.BAD_REQUEST),

    // 유저 관련 에러
    USER_NOT_FOUND("U001", "존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("U002", "이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("U003", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    ROOM_NOT_FOUND("U004", "존재하지 않는 방입니다.", HttpStatus.NOT_FOUND),
    NO_ROOMS_IN_SERVER("U005", "방이 없습니다.", HttpStatus.NOT_FOUND),
    USER_ALREADY_IN_ROOM("U006", "이미 방에 들어가 있는 상황입니다.", HttpStatus.CONFLICT),
    INVALID_ROOM_CAPACITY("U007", "방의 정원이 알맞지 않습니다.", HttpStatus.BAD_REQUEST),

    // 인증 관련 에러
    UNAUTHORIZED("A001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("A002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN("A003", "토큰이 만료되었습니다.", HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }


}
