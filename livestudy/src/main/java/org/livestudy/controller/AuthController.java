package org.livestudy.controller;

import org.livestudy.dto.UserLoginRequest;
import org.livestudy.dto.UserLoginResponse;
import org.livestudy.dto.UserSignupRequest;
import org.livestudy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 이메일 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequest request){
        log.info("🔥 회원가입 요청 도착: {}", request.getEmail());
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // OAuth2 로그인 URL 제공
    @GetMapping("/oauth2/url/{provider}")
    public ResponseEntity<Map<String, String>> getOAuth2LoginUrl(@PathVariable String provider) {
        String authUrl = String.format("/api/auth/oauth2/authorize/%s", provider.toLowerCase());
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }
}
