package org.livestudy.controller;

import org.livestudy.dto.UserLoginRequest;
import org.livestudy.dto.UserLoginResponse;
import org.livestudy.dto.UserSignupRequest;
import org.livestudy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // 이메일 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequest request){
        System.out.println("🔥 회원가입 요청 도착: " + request.getEmail());
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
