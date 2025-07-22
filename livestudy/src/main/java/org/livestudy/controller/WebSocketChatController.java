package org.livestudy.controller;

import org.livestudy.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void handleChat(@Payload ChatMessage message, Principal principal) {
        String sender = principal.getName();
        message.setSender(sender);
        messagingTemplate.convertAndSend("/topic/chat.room." + message.getRoomId(), message);
    }
}
