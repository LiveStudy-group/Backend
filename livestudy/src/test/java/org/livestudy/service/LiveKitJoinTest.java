package org.livestudy.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LiveKitJoinTest {

    private final String apiKey = "testApiKey";

    private final String apiSecret = "testApiSecret";

    private LiveKitTokenService liveKitTokenService;

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp(){
        liveKitTokenService = new LiveKitTokenService(apiKey, apiSecret);
    }

    @Test
    void generateToken_success(){
        // given

        String roomId = "roomId";
        String userId = "userId";

        // when
        String token = liveKitTokenService.generateToken(roomId, userId);

        // then

        assertThat(token).isNotNull();
        assertThat(token).isInstanceOf(String.class);
        assertThat(token).isNotBlank();
    }

    @Test
    void generateToken_fail_whenUserIdIsEmpty() {
        CustomException exception = assertThrows(CustomException.class, () ->
                liveKitTokenService.generateToken(null, "test-room1")
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void generateToken_fail_InvalidInput() {
        // 준비: 토큰 생성용 key, secret 직접 세팅
        String apiKey = "test-key";
        String apiSecret = "test-secret";

        String userId = "123";
        String roomId = "test-room";

        // AccessToken 생성 (만료 시간을 현재보다 과거로 설정하는 건 직접 불가할 수 있음)
        AccessToken token = new AccessToken(apiKey, apiSecret);
        token.setIdentity(userId);
        token.addGrants(new RoomName(roomId));

        String jwt = token.toJwt();

        System.out.println("🔑 generated JWT: " + jwt);

        // JwtTokenProvider 준비 - 직접 secretKey 주입, 초기화 메서드 호출
        JwtTokenProvider jwtProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtProvider, "secretKey", "your-256-bit-secret-your-256-bit-secret");
        jwtProvider.init();

        // 다른 토큰 하나 설정
        String anotherJwt = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiZXhwIjoxNjAwMDAwMDAwfQ.DummySignature";

        CustomException exception = assertThrows(CustomException.class, () -> {
            jwtProvider.validateToken(anotherJwt);
        });

        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
    }

    @Test
    void validateToken_fail_whenExpired() {
        // secretKey 세팅 (JwtTokenProvider와 동일해야 함)
        String secretKey = "your-256-bit-secret-your-256-bit-secret"; // 32바이트 이상이어야 함
        JwtTokenProvider jwtProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtProvider, "secretKey", secretKey);
        jwtProvider.init();

        // 만료된 토큰 직접 생성
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000 * 60); // 1분 전

        String expiredJwt = Jwts.builder()
                .setSubject("testUser")
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        CustomException exception = assertThrows(CustomException.class, () -> {
            jwtProvider.validateToken(expiredJwt);
        });

        assertEquals(ErrorCode.EXPIRED_TOKEN, exception.getErrorCode());
    }





}
