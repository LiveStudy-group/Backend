package org.livestudy.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.websocket.dto.*;
import org.livestudy.websocket.service.PresenceService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class RoomWebSocketController {

    private final PresenceService presence;
    private final SimpMessagingTemplate broker;

    // 입장
    @MessageMapping("/api/study-room/enter")
    public String join(@Payload BaseMsg<JoinPayload> msg,
                     SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        String roomId = (String) accessor.getSessionAttributes().get("roomId");
        JoinPayload join = msg.getPayload();

        // 유효성 검사
        if (!sessionUser.equals(join.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }

        if (roomId == null) {
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }

        presence.join(roomId, sessionUser);

        return roomId;
    }

    // 퇴장
    @MessageMapping("/api/study-room/exit")
    public void exit(@Payload BaseMsg<ExitPayload> msg,
                     SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        String roomId = (String) accessor.getSessionAttributes().get("roomId");
        ExitPayload exit = msg.getPayload();

        // 유효성 검사
        if (!sessionUser.equals(exit.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }

        if (roomId == null) {
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }

        presence.exit(roomId, sessionUser);
    }

    private <T> BaseMsg<T> wrap(MsgType type, T payload) {
        BaseMsg<T> m = new BaseMsg<>();
        m.setType(type);
        m.setPayload(payload);
        m.setTimeStamp(Instant.now());
        return m;
    }
}

