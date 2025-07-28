package org.livestudy.service.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String msg = new String(message.getBody());

        log.info("{} 채널로부터 {} 메시지를 받았습니다", channel, message);

        if (channel.startsWith("restriction:")) {
            handleRestrictionMessage(channel, msg);
        } else if (channel.startsWith("systemMessage:")) {
            handleSystemMessage(channel, msg);
        }
    }

    public void handleRestrictionMessage(String channel, String msg) {

        String reportedId = channel.substring("restriction:".length());

        messagingTemplate.convertAndSendToUser(reportedId, "/queue/restriction", msg);
        log.info("{} 유저에게 {} 강퇴 메시지를 보냈습니다.", reportedId, msg);
    }

    public void handleSystemMessage(String channel, String systemMessage) {

        String roomId = channel.substring("systemMessage:".length());

        messagingTemplate.convertAndSend("/topic/studyroom/" + roomId + "/systemMsg", systemMessage);
        log.info("{} 번 공부방에 {} 메시지를 보냈습니다.", roomId, systemMessage);
    }
}
