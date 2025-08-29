package org.livestudy.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.livestudy.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomException ex,
                                                         HttpServletRequest request) {

        ErrorCode errorCode = ex.getErrorCode();

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now().toString(),
                errorCode.getHttpStatus().value(),
                errorCode.getHttpStatus().getReasonPhrase(),
                errorCode.getCode(),
                errorCode.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
