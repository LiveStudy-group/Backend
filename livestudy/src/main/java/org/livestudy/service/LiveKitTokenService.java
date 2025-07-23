package org.livestudy.service;

import io.livekit.server.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LiveKitTokenService {

    private static final Logger log = LoggerFactory.getLogger(LiveKitTokenService.class);

    @Value("${livekit.api-key}")
    private String apiKey;

    @Value("${livekit.api-secret}")
    private String apiSecret;

    public String generateToken(String userId){

        String roomId = "test-room";


        // 토큰 생성
        AccessToken token = new AccessToken(apiKey, apiSecret);

        token.setIdentity(userId);

        token.addGrants(new RoomJoin(true));
        token.addGrants(new RoomName(roomId));
        token.addGrants(new CanPublish(true));
        token.addGrants(new CanSubscribe(true));
        token.addGrants(new CanPublishData(true));

        log.info("generateToken called with roomId = {}, userId = {}", roomId, userId);


        return token.toJwt();
    }
}
