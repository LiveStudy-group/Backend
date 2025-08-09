package org.livestudy.service.livekit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.livekit.server.*;
import org.livestudy.domain.user.User;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.security.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;


@Service
public class LiveKitTokenService {

    private static final Logger log = LoggerFactory.getLogger(LiveKitTokenService.class);



    private final String apiKey;


    private final String apiSecret;

    private final JwtParser jwtParser;

    public LiveKitTokenService(
            @Value("${livekit.api-key}")String apiKey,
            @Value("${livekit.api-secret}")String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;


        byte[] keyBytes = apiSecret.getBytes(StandardCharsets.UTF_8);

        Key key = Keys.hmacShaKeyFor(keyBytes);

        this.jwtParser = Jwts.parser().setSigningKey(key).build();
    }


    public String generateToken(String userId, String roomId){

        if(userId == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        if(roomId == null) {
            throw new CustomException(ErrorCode.ROOM_NOT_FOUND);
        }

        // 토큰 생성
        AccessToken token = new AccessToken(apiKey, apiSecret);

        token.setIdentity(userId);

        token.addGrants(new RoomJoin(true));
        token.addGrants(new RoomName(roomId));
        token.addGrants(new RoomCreate(true));
        token.addGrants(new CanPublish(true));
        token.addGrants(new CanSubscribe(true));
        token.addGrants(new CanPublishData(true));

        log.info("generateToken called with roomId = {}, userId = {}", roomId, userId);


        return token.toJwt();
    }

    public boolean validateToken(String token){
        try{
            jwtParser.parseSignedClaims(token);
            return true;
        } catch( JwtException | IllegalArgumentException e){
            log.warn("Invalid LiveKit JWT token : {}", e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        String email = claims.getSubject();
        Long userId = claims.get("userId", Long.class);

        SecurityUser principal = new SecurityUser(
                User.builder()
                        .id(userId)
                        .email(email)
                        .build()
        );

        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    private Claims parseClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }
}
