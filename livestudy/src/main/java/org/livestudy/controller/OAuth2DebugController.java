package org.livestudy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class OAuth2DebugController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/auth-info")
    public ResponseEntity<Map<String, Object>> getAuthInfo(@AuthenticationPrincipal SecurityUser user) {
        log.info("🔍 현재 인증된 사용자 정보 확인");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", auth.isAuthenticated());
        response.put("authType", auth.getClass().getSimpleName());
        response.put("principalType", auth.getPrincipal().getClass().getSimpleName());

        if (user != null) {
            response.put("userId", user.getUser().getId());
            response.put("email", user.getUser().getEmail());
            response.put("nickname", user.getUser().getNickname());
            response.put("socialProvider", user.getUser().getSocialProvider());
            response.put("hasPassword", user.getUser().getPassword() != null);

            // OAuth2 속성이 있는지 확인
            if (user.getAttributes() != null) {
                response.put("hasOAuth2Attributes", true);
                response.put("oAuth2AttributesKeys", user.getAttributes().keySet());
            } else {
                response.put("hasOAuth2Attributes", false);
            }

            log.info("사용자 ID: {}, 이메일: {}, 소셜프로바이더: {}",
                    user.getUser().getId(), user.getUser().getEmail(), user.getUser().getSocialProvider());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/generate-token")
    public ResponseEntity<Map<String, Object>> generateTokenTest(@AuthenticationPrincipal SecurityUser user) {
        log.info("JWT 토큰 생성 테스트");

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "인증되지 않음"));
        }

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String token = jwtTokenProvider.generateToken(auth);

            // 생성된 토큰으로 다시 인증 정보 추출해보기
            Authentication parsedAuth = jwtTokenProvider.getAuthentication(token);
            SecurityUser parsedUser = (SecurityUser) parsedAuth.getPrincipal();

            Map<String, Object> response = new HashMap<>();
            response.put("tokenGenerated", true);
            response.put("tokenValid", jwtTokenProvider.validateToken(token));
            response.put("originalUserId", user.getUser().getId());
            response.put("parsedUserId", parsedUser.getUser().getId());
            response.put("originalEmail", user.getUser().getEmail());
            response.put("parsedEmail", parsedUser.getUser().getEmail());
            response.put("token", token.substring(0, 20) + "..."); // 일부만 표시

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("JWT 토큰 생성 실패", e);
            return ResponseEntity.status(500).body(Map.of(
                    "error", "토큰 생성 실패",
                    "message", e.getMessage()
            ));
        }
    }
}