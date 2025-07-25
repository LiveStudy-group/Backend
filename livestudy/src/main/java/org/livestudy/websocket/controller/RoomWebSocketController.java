package org.livestudy.websocket.controller;

import lombok.RequiredArgsConstructor;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.service.ChatService;
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
    private final ChatService chatService;

    // 입장
    @MessageMapping("/api/study-room/enter")
    public String join(@Payload BaseMsg<JoinPayload> msg,
                     SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        String assignedRoomId = (String) accessor.getSessionAttributes().get("roomId");
        JoinPayload join = msg.getPayload();

        // 유효성 검사
        if (!sessionUser.equals(join.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }

        presence.join(assignedRoomId, sessionUser);

        return assignedRoomId;
    }

    // 퇴장
    @MessageMapping("/api/study-room/exit")
    public void exit(@Payload BaseMsg<ExitPayload> msg,
                     SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        String roomId = (String) accessor.getSessionAttributes().get("roomId");

        if (roomId == null) {
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }

        ExitPayload exit = msg.getPayload();

        if (!sessionUser.equals(exit.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }

        presence.exit(roomId, sessionUser);
    }

    // 채팅
    @MessageMapping("/api/study-room/chat")
    public void chat(@Payload BaseMsg<ChatPayload> msg,
                     SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        String roomId = (String) accessor.getSessionAttributes().get("roomId");
        ChatPayload chat = msg.getPayload();

        // 유효성 검사
        if (!sessionUser.equals(chat.getUserId())) {
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }

        if (roomId == null || !roomId.equals(chat.getRoomId())) {
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }

        broker.convertAndSend("/topic/" + roomId, wrap(MsgType.chat, chat));

        chatService.saveChat(roomId, sessionUser, chat.getMessage());
    }

    private <T> BaseMsg<T> wrap(MsgType type, T payload) {
        BaseMsg<T> m = new BaseMsg<>();
        m.setType(type);
        m.setPayload(payload);
        m.setTimeStamp(Instant.now());
        return m;
    }
}

