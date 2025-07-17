package org.livestudy.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.websocket.dto.BaseMsg;
import org.livestudy.websocket.dto.ExitPayload;
import org.livestudy.websocket.dto.JoinPayload;
import org.livestudy.websocket.dto.MsgType;
import org.livestudy.websocket.service.PresenceService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
    @MessageMapping("/room/{roomId}/join")
    public void join(@DestinationVariable String roomId,
                     @Payload BaseMsg<JoinPayload> msg,
                     SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        JoinPayload join = msg.getPayload();
        //강퇴 여부
        boolean ok = presence.join(roomId, sessionUser);

        if (!sessionUser.equals(join.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }

        if (!ok) {
            throw new CustomException(ErrorCode.USER_BANNED);
        }

        accessor.getSessionAttributes().put("roomId", roomId);

        broker.convertAndSend("/topic/"+roomId, wrap(MsgType.join, join));
    }

    // 퇴장
    @MessageMapping("/room/{roomId}/exit")
    public void exit(@DestinationVariable String roomId,
                     @Payload BaseMsg<ExitPayload> msg,
                     SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        ExitPayload exit = msg.getPayload();

        if (!sessionUser.equals(exit.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }

        presence.exit(roomId, sessionUser, exit.isBanned());
        broker.convertAndSend("/topic/"+roomId, wrap(MsgType.exit, exit));
    }

    private <T> BaseMsg<T> wrap(MsgType type, T payload) {
        BaseMsg<T> m = new BaseMsg<>();
        m.setType(type);
        m.setPayload(payload);
        m.setTimeStamp(Instant.now());
        return m;
    }
}

