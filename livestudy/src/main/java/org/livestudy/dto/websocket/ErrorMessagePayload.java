package org.livestudy.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livestudy.exception.ErrorCode;

/**
 * WebSocket 에러 메시지 전송 시 사용되는 페이로드
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessagePayload {

    /**
     * 에러 발생 사용자 ID
     */
    private String userId;

    /**
     * 에러 코드 (예: USER_NOT_FOUND, ROOM_FULL 등)
     */
    private String errorCode;

    /**
     * 에러 제목 (사용자에게 표시될 에러 제목)
     */
    private String errorTitle;

    /**
     * 에러 메시지 (사용자에게 표시될 구체적인 설명)
     */
    private String errorMessage;

    /**
     * HTTP 상태 코드
     */
    private Integer httpStatus;

    /**
     * 에러 발생 시간 (밀리초 타임스탬프)
     */
    private Long timestamp;

    /**
     * 추가적인 에러 정보 (예: 유효성 검증 실패 필드들)
     */
    private Object details;

    /**
     * 클라이언트에서 재시도 가능한지 여부
     */
    private Boolean retryable;

    /**
     * 에러 발생 경로 (어떤 WebSocket 경로에서 발생했는지)
     */
    private String path;

    /**
     * ErrorCode enum을 사용한 에러 페이로드 생성
     */
    public static ErrorMessagePayload of(String userId, ErrorCode errorCode) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode(errorCode.getCode())
                .errorTitle("오류 발생")
                .errorMessage(errorCode.getMessage())
                .httpStatus(errorCode.getHttpStatus().value())
                .timestamp(System.currentTimeMillis())
                .retryable(isRetryable(errorCode))
                .build();
    }

    /**
     * ErrorCode와 경로 정보를 포함한 에러 페이로드 생성
     */
    public static ErrorMessagePayload of(String userId, ErrorCode errorCode, String path) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode(errorCode.getCode())
                .errorTitle("오류 발생")
                .errorMessage(errorCode.getMessage())
                .httpStatus(errorCode.getHttpStatus().value())
                .timestamp(System.currentTimeMillis())
                .retryable(isRetryable(errorCode))
                .path(path)
                .build();
    }

    /**
     * 커스텀 에러 메시지로 에러 페이로드 생성
     */
    public static ErrorMessagePayload of(String userId, String errorCode, String errorMessage) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode(errorCode)
                .errorTitle("오류 발생")
                .errorMessage(errorMessage)
                .httpStatus(400)
                .timestamp(System.currentTimeMillis())
                .retryable(false)
                .build();
    }

    /**
     * 상세 정보가 포함된 에러 페이로드 생성
     */
    public static ErrorMessagePayload of(String userId, ErrorCode errorCode, String path, Object details) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode(errorCode.getCode())
                .errorTitle("오류 발생")
                .errorMessage(errorCode.getMessage())
                .httpStatus(errorCode.getHttpStatus().value())
                .timestamp(System.currentTimeMillis())
                .retryable(isRetryable(errorCode))
                .path(path)
                .details(details)
                .build();
    }

    /**
     * 유효성 검증 실패 에러 페이로드 생성
     */
    public static ErrorMessagePayload validationError(String userId, String path, Object validationDetails) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("VALIDATION_ERROR")
                .errorTitle("입력값 오류")
                .errorMessage("입력값을 확인해주세요.")
                .httpStatus(400)
                .timestamp(System.currentTimeMillis())
                .retryable(true)
                .path(path)
                .details(validationDetails)
                .build();
    }

    /**
     * 인증 실패 에러 페이로드 생성
     */
    public static ErrorMessagePayload authenticationError(String userId) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("AUTHENTICATION_ERROR")
                .errorTitle("인증 실패")
                .errorMessage("로그인이 필요합니다.")
                .httpStatus(401)
                .timestamp(System.currentTimeMillis())
                .retryable(false)
                .build();
    }

    /**
     * 권한 부족 에러 페이로드 생성
     */
    public static ErrorMessagePayload authorizationError(String userId, String action) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("AUTHORIZATION_ERROR")
                .errorTitle("권한 부족")
                .errorMessage(action + "에 대한 권한이 없습니다.")
                .httpStatus(403)
                .timestamp(System.currentTimeMillis())
                .retryable(false)
                .build();
    }

    /**
     * 서버 내부 에러 페이로드 생성
     */
    public static ErrorMessagePayload internalServerError(String userId) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("INTERNAL_SERVER_ERROR")
                .errorTitle("서버 오류")
                .errorMessage("일시적인 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                .httpStatus(500)
                .timestamp(System.currentTimeMillis())
                .retryable(true)
                .build();
    }

    /**
     * 연결 관련 에러 페이로드 생성
     */
    public static ErrorMessagePayload connectionError(String userId, String reason) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("CONNECTION_ERROR")
                .errorTitle("연결 오류")
                .errorMessage("연결에 문제가 발생했습니다: " + reason)
                .httpStatus(400)
                .timestamp(System.currentTimeMillis())
                .retryable(true)
                .build();
    }

    /**
     * 스터디룸 관련 에러 페이로드 생성
     */
    public static ErrorMessagePayload roomError(String userId, String errorMessage) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("ROOM_ERROR")
                .errorTitle("스터디룸 오류")
                .errorMessage(errorMessage)
                .httpStatus(400)
                .timestamp(System.currentTimeMillis())
                .retryable(false)
                .build();
    }

    /**
     * 타이머 관련 에러 페이로드 생성
     */
    public static ErrorMessagePayload timerError(String userId, String errorMessage) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("TIMER_ERROR")
                .errorTitle("타이머 오류")
                .errorMessage(errorMessage)
                .httpStatus(400)
                .timestamp(System.currentTimeMillis())
                .retryable(true)
                .build();
    }

    /**
     * 채팅 관련 에러 페이로드 생성
     */
    public static ErrorMessagePayload chatError(String userId, String errorMessage) {
        return ErrorMessagePayload.builder()
                .userId(userId)
                .errorCode("CHAT_ERROR")
                .errorTitle("채팅 오류")
                .errorMessage(errorMessage)
                .httpStatus(400)
                .timestamp(System.currentTimeMillis())
                .retryable(true)
                .build();
    }

    /**
     * 에러코드에 따른 재시도 가능 여부 판단
     */
    private static Boolean isRetryable(ErrorCode errorCode) {
        // 재시도 가능한 에러 코드들
        String[] retryableErrors = {
                "INTERNAL_SERVER_ERROR",
                "SERVICE_UNAVAILABLE",
                "REQUEST_TIMEOUT",
                "CONNECTION_ERROR",
                "TIMER_NOT_RUNNING"
        };

        String code = errorCode.getCode();
        for (String retryableError : retryableErrors) {
            if (code.contains(retryableError)) {
                return true;
            }
        }

        // HTTP 5xx 에러는 일반적으로 재시도 가능
        return errorCode.getHttpStatus().is5xxServerError();
    }
}