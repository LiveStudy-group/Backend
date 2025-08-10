package org.livestudy.websocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        if (sessionUser == null || !sessionUser.equals(join.getUserId())) {
            log.error("유저 ID 불일치. 세션 userId: {}, 페이로드 userId: {}",
                    sessionUser, join.getUserId());
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }
        if (assignedRoomId == null || !assignedRoomId.equals(join.getRoomId())) {
            log.error("스터디룸 ID 불일치. 세션 roomId: {}, 페이로드 roomId: {}, userId: {}",
                    assignedRoomId, join.getRoomId(), sessionUser);
            throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
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
        ExitPayload exit = msg.getPayload();

        // 유효성 검사
        if (sessionUser == null || !sessionUser.equals(exit.getUserId())) {
            log.error("유저 ID 불일치. 세션 userId: {}, 페이로드 userId: {}", sessionUser, exit.getUserId());
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }
        if (roomId == null || !roomId.equals(exit.getRoomId())) {
            log.error("유저가 방에 존재하지 않습니다. 세션 roomId: {}, 페이로드 roomId: {}, userId: {}",
                    roomId, exit.getRoomId(), sessionUser);
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
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
        if (sessionUser == null || !sessionUser.equals(chat.getUserId())) {
            log.error("유저 ID 불일치. 세션 userId: {}, 페이로드 userId: {}", sessionUser, chat.getUserId());
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }
        if (roomId == null || !roomId.equals(chat.getRoomId())) {
            log.error("유저가 방에 존재하지 않습니다. 세션 roomId: {}, 페이로드 roomId: {}, userId: {}",
                    roomId, chat.getRoomId(), sessionUser);
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

