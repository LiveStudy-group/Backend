package org.livestudy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.dto.timer.TimerResponse;
import org.livestudy.dto.websocket.*;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.service.TimerService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoomController {

    private final SimpMessagingTemplate broker;
    private final TimerService timerService;

    // === 사용자 입장/퇴장 관련 ===

    @MessageMapping("/room/{roomId}/join")
    public void joinRoom(@DestinationVariable String roomId,
                         @Payload BaseMsg<UserJoinPayload> msg,
                         SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        UserJoinPayload joinPayload = msg.getPayload();

        log.info("방 입장 요청: roomId={}, userId={}, nickname={}",
                roomId, joinPayload.getUserId(), joinPayload.getNickname());

        validateUser(sessionUser, joinPayload.getUserId());

        try {
            // 방 입장 알림을 모든 참여자에게 전송
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.USER_JOIN, joinPayload));

            // 시스템 메시지 전송
            sendSystemMessage(roomId, joinPayload.getNickname() + "님이 입장했습니다.");

            log.info("방 입장 완료: roomId={}, userId={}", roomId, joinPayload.getUserId());

        } catch (Exception e) {
            log.error("방 입장 실패: roomId={}, userId={}, error={}",
                    roomId, joinPayload.getUserId(), e.getMessage());
            sendErrorToUser(sessionUser, "방 입장에 실패했습니다: " + e.getMessage());
            throw new CustomException(ErrorCode.WEBSOCKET_MESSAGE_ERROR);
        }
    }

    @MessageMapping("/room/{roomId}/leave")
    public void leaveRoom(@DestinationVariable String roomId,
                          @Payload BaseMsg<UserLeavePayload> msg,
                          SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        UserLeavePayload leavePayload = msg.getPayload();

        log.info("방 퇴장 요청: roomId={}, userId={}, nickname={}",
                roomId, leavePayload.getUserId(), leavePayload.getNickname());

        validateUser(sessionUser, leavePayload.getUserId());

        try {
            // 타이머가 실행 중이면 먼저 종료
            try {
                timerService.stopFocus(Long.parseLong(leavePayload.getUserId()), Long.parseLong(roomId));
                log.info("퇴장 시 타이머 자동 종료: userId={}, roomId={}", leavePayload.getUserId(), roomId);
            } catch (Exception timerException) {
                log.warn("퇴장 시 타이머 종료 실패 (무시함): {}", timerException.getMessage());
            }

            // 방 퇴장 알림을 모든 참여자에게 전송
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.USER_LEAVE, leavePayload));

            // 시스템 메시지 전송
            sendSystemMessage(roomId, leavePayload.getNickname() + "님이 퇴장했습니다.");

            log.info("방 퇴장 완료: roomId={}, userId={}", roomId, leavePayload.getUserId());

        } catch (Exception e) {
            log.error("방 퇴장 실패: roomId={}, userId={}, error={}",
                    roomId, leavePayload.getUserId(), e.getMessage());
            // 퇴장은 실패해도 계속 진행 (사용자에게는 에러 전송)
            sendErrorToUser(sessionUser, "방 퇴장 처리 중 오류가 발생했습니다.");
        }
    }

    // === 집중 타이머 관련 ===

    @MessageMapping("/room/{roomId}/focus/start")
    public void startFocus(@DestinationVariable String roomId,
                           @Payload BaseMsg<FocusStartPayload> msg,
                           SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        FocusStartPayload focusStart = msg.getPayload();

        log.info("집중 시작 WebSocket 요청: roomId={}, userId={}, nickname={}",
                roomId, focusStart.getUserId(), focusStart.getNickname());

        validateUser(sessionUser, focusStart.getUserId());

        try {
            // TimerService를 통해 집중 시작 처리
            TimerResponse timerResponse = timerService.startFocus(
                    Long.parseLong(focusStart.getUserId()),
                    Long.parseLong(roomId)
            );

            // 성공 응답을 해당 사용자에게 전송
            sendSuccessToUser(sessionUser, "focus_start", timerResponse);

            // 방의 모든 참여자에게 집중 시작 알림
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.FOCUS_START, focusStart));

            log.info("집중 시작 완료: roomId={}, userId={}", roomId, focusStart.getUserId());

        } catch (Exception e) {
            log.error("집중 시작 실패: roomId={}, userId={}, error={}",
                    roomId, focusStart.getUserId(), e.getMessage());
            sendErrorToUser(sessionUser, "집중 시작에 실패했습니다: " + e.getMessage());
            throw e;
        }
    }

    @MessageMapping("/room/{roomId}/focus/pause")
    public void pauseFocus(@DestinationVariable String roomId,
                           @Payload BaseMsg<FocusEndPayload> msg,
                           SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        FocusEndPayload focusEnd = msg.getPayload();

        log.info("집중 일시정지 WebSocket 요청: roomId={}, userId={}, nickname={}",
                roomId, focusEnd.getUserId(), focusEnd.getNickname());

        validateUser(sessionUser, focusEnd.getUserId());

        try {
            // TimerService를 통해 집중 일시정지 처리
            TimerResponse timerResponse = timerService.pauseFocus(
                    Long.parseLong(focusEnd.getUserId()),
                    Long.parseLong(roomId)
            );

            // 성공 응답을 해당 사용자에게 전송
            sendSuccessToUser(sessionUser, "focus_pause", timerResponse);

            // 방의 모든 참여자에게 집중 일시정지 알림
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.FOCUS_PAUSE, focusEnd));

            log.info("집중 일시정지 완료: roomId={}, userId={}", roomId, focusEnd.getUserId());

        } catch (Exception e) {
            log.error("집중 일시정지 실패: roomId={}, userId={}, error={}",
                    roomId, focusEnd.getUserId(), e.getMessage());
            sendErrorToUser(sessionUser, "집중 일시정지에 실패했습니다: " + e.getMessage());
            throw e;
        }
    }

    @MessageMapping("/room/{roomId}/focus/resume")
    public void resumeFocus(@DestinationVariable String roomId,
                            @Payload BaseMsg<FocusStartPayload> msg,
                            SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        FocusStartPayload focusStart = msg.getPayload();

        log.info("집중 재개 WebSocket 요청: roomId={}, userId={}, nickname={}",
                roomId, focusStart.getUserId(), focusStart.getNickname());

        validateUser(sessionUser, focusStart.getUserId());

        try {
            // TimerService를 통해 집중 재개 처리
            TimerResponse timerResponse = timerService.resumeFocus(
                    Long.parseLong(focusStart.getUserId()),
                    Long.parseLong(roomId)
            );

            // 성공 응답을 해당 사용자에게 전송
            sendSuccessToUser(sessionUser, "focus_resume", timerResponse);

            // 방의 모든 참여자에게 집중 재개 알림
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.FOCUS_RESUME, focusStart));

            log.info("집중 재개 완료: roomId={}, userId={}", roomId, focusStart.getUserId());

        } catch (Exception e) {
            log.error("집중 재개 실패: roomId={}, userId={}, error={}",
                    roomId, focusStart.getUserId(), e.getMessage());
            sendErrorToUser(sessionUser, "집중 재개에 실패했습니다: " + e.getMessage());
            throw e;
        }
    }

    @MessageMapping("/room/{roomId}/focus/stop")
    public void stopFocus(@DestinationVariable String roomId,
                          @Payload BaseMsg<FocusEndPayload> msg,
                          SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        FocusEndPayload focusEnd = msg.getPayload();

        log.info("집중 종료 WebSocket 요청: roomId={}, userId={}, nickname={}",
                roomId, focusEnd.getUserId(), focusEnd.getNickname());

        validateUser(sessionUser, focusEnd.getUserId());

        try {
            // TimerService를 통해 집중 종료 처리
            TimerResponse timerResponse = timerService.stopFocus(
                    Long.parseLong(focusEnd.getUserId()),
                    Long.parseLong(roomId)
            );

            // 성공 응답을 해당 사용자에게 전송
            sendSuccessToUser(sessionUser, "focus_stop", timerResponse);

            // 방의 모든 참여자에게 집중 종료 알림
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.FOCUS_STOP, focusEnd));

            log.info("집중 종료 완료: roomId={}, userId={}", roomId, focusEnd.getUserId());

        } catch (Exception e) {
            log.error("집중 종료 실패: roomId={}, userId={}, error={}",
                    roomId, focusEnd.getUserId(), e.getMessage());
            sendErrorToUser(sessionUser, "집중 종료에 실패했습니다: " + e.getMessage());
            throw e;
        }
    }

    // === 사용자 상태 업데이트 ===

    @MessageMapping("/room/{roomId}/status/update")
    public void updateUserStatus(@DestinationVariable String roomId,
                                 @Payload BaseMsg<UserStatusPayload> msg,
                                 SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        UserStatusPayload statusPayload = msg.getPayload();

        log.info("사용자 상태 업데이트: roomId={}, userId={}, status={}",
                roomId, statusPayload.getUserId(), statusPayload.getStatus());

        validateUser(sessionUser, statusPayload.getUserId());

        try {
            // 방의 모든 참여자에게 상태 업데이트 알림
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.USER_STATUS_UPDATE, statusPayload));

            log.info("사용자 상태 업데이트 완료: roomId={}, userId={}", roomId, statusPayload.getUserId());

        } catch (Exception e) {
            log.error("사용자 상태 업데이트 실패: roomId={}, userId={}, error={}",
                    roomId, statusPayload.getUserId(), e.getMessage());
            sendErrorToUser(sessionUser, "상태 업데이트에 실패했습니다: " + e.getMessage());
            throw new CustomException(ErrorCode.WEBSOCKET_MESSAGE_ERROR);
        }
    }

    // === 채팅 메시지 ===

    @MessageMapping("/room/{roomId}/chat")
    public void sendChatMessage(@DestinationVariable String roomId,
                                @Payload BaseMsg<ChatMessagePayload> msg,
                                SimpMessageHeaderAccessor accessor) {

        String sessionUser = (String) accessor.getSessionAttributes().get("userId");
        ChatMessagePayload chatPayload = msg.getPayload();

        log.info("채팅 메시지: roomId={}, userId={}, message={}",
                roomId, chatPayload.getUserId(), chatPayload.getMessage());

        validateUser(sessionUser, chatPayload.getUserId());

        try {
            // 타임스탬프 설정
            chatPayload.setTimestamp(System.currentTimeMillis());

            // 방의 모든 참여자에게 채팅 메시지 전송
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.CHAT_MESSAGE, chatPayload));

            log.info("채팅 메시지 전송 완료: roomId={}, userId={}", roomId, chatPayload.getUserId());

        } catch (Exception e) {
            log.error("채팅 메시지 전송 실패: roomId={}, userId={}, error={}",
                    roomId, chatPayload.getUserId(), e.getMessage());
            sendErrorToUser(sessionUser, "채팅 메시지 전송에 실패했습니다: " + e.getMessage());
            throw new CustomException(ErrorCode.WEBSOCKET_MESSAGE_ERROR);
        }
    }

    // === Private Helper Methods ===

    /**
     * 사용자 인증 검증
     */
    private void validateUser(String sessionUser, String payloadUser) {
        if (sessionUser == null || !sessionUser.equals(payloadUser)) {
            log.error("사용자 ID 불일치: session={}, payload={}", sessionUser, payloadUser);
            throw new CustomException(ErrorCode.USER_ID_MISMATCH);
        }
    }

    /**
     * WebSocket 메시지를 래핑하는 유틸리티 메서드
     * BaseMsg.create() 편의 메서드 사용
     */
    private <T> BaseMsg<T> wrap(MsgType msgType, T payload) {
        return BaseMsg.create(msgType, payload);
    }

    /**
     * 특정 사용자에게 에러 메시지 전송
     */
    private void sendErrorToUser(String userId, String errorMessage) {
        try {
            ErrorMessagePayload errorPayload = ErrorMessagePayload.of(userId, "WEBSOCKET_ERROR", errorMessage);
            BaseMsg<ErrorMessagePayload> errorMsg = wrap(MsgType.ERROR, errorPayload);

            broker.convertAndSendToUser(userId, "/queue/errors", errorMsg);
            log.debug("에러 메시지 전송: userId={}, message={}", userId, errorMessage);

        } catch (Exception e) {
            log.error("에러 메시지 전송 실패: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 특정 사용자에게 성공 응답 전송
     */
    private void sendSuccessToUser(String userId, String action, Object data) {
        try {
            Map<String, Object> successMsg = Map.of(
                    "type", "SUCCESS",
                    "action", action,
                    "data", data,
                    "timestamp", System.currentTimeMillis()
            );

            broker.convertAndSendToUser(userId, "/queue/responses", successMsg);
            log.debug("성공 응답 전송: userId={}, action={}", userId, action);

        } catch (Exception e) {
            log.error("성공 응답 전송 실패: userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 시스템 메시지를 방 전체에 전송
     */
    private void sendSystemMessage(String roomId, String message) {
        try {
            ChatMessagePayload systemMsg = ChatMessagePayload.systemMessage(message);
            broker.convertAndSend("/topic/" + roomId, wrap(MsgType.SYSTEM_MESSAGE, systemMsg));
            log.debug("시스템 메시지 전송: roomId={}, message={}", roomId, message);

        } catch (Exception e) {
            log.error("시스템 메시지 전송 실패: roomId={}, error={}", roomId, e.getMessage());
        }
    }
}