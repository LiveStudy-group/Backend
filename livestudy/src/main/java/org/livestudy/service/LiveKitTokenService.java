package org.livestudy.service;

import io.livekit.server.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LiveKitTokenService {

    @Value("${livekit.api-key}")
    private String apiKey;

    @Value("${livekit.api-secret}")
    private String apiSecret;

    public String generateToken(String roomId, String userId){

        // 토큰 생성
        AccessToken token = new AccessToken(apiKey, apiSecret);

        token.setIdentity(userId);

        token.addGrants(new RoomJoin(true));
        token.addGrants(new RoomName(roomId));
        token.addGrants(new CanPublish(true));
        token.addGrants(new CanSubscribe(true));
        token.addGrants(new CanPublishData(true));


        return token.toJwt();
    }
}
