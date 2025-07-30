package org.livestudy.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.websocket.dto.BaseMsg;
import org.livestudy.websocket.dto.ErrorPayload;
import org.livestudy.websocket.dto.MsgType;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final SimpMessagingTemplate broker;

    @MessageExceptionHandler(CustomException.class)
    public void CustomHandle(SimpMessageHeaderAccessor accessor, CustomException e) {
        sendError(accessor, e.getErrorCode());
    }

    @MessageExceptionHandler(Exception.class)
    public void UnknownHandle(SimpMessageHeaderAccessor accessor, Exception e) {
        sendError(accessor, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public void sendError(SimpMessageHeaderAccessor accessor, ErrorCode code) {
        ErrorPayload payload = new ErrorPayload();
        payload.setCode(code.getCode());
        payload.setMessage(code.getMessage());

        BaseMsg<ErrorPayload> error = new BaseMsg<>();
        error.setType(MsgType.error);
        error.setPayload(payload);
        error.setTimeStamp(Instant.now());

        String userId = (String) accessor.getSessionAttributes().get("userId");
        broker.convertAndSendToUser("_system_", "/queue/errors", error);
    }
}
