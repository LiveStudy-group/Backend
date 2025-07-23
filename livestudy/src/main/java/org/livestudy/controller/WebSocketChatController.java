package org.livestudy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;



@Controller
public class WebSocketChatController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketChatController.class);

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public String broadcast(String message) {
        logger.info("broadcast message: {}", message);
        logger.info("내용 : {}", message);
        return "응답: " + message;
    }
}
