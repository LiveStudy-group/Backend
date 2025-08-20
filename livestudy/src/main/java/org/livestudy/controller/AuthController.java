package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.livestudy.dto.UserLoginRequest;
import org.livestudy.dto.UserLoginResponse;
import org.livestudy.dto.UserSignupRequest;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.livestudy.service.RefreshTokenService;
import org.livestudy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 API", description = "회원가입, 로그인 인증 관련 API")
public class  AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService,
                          RefreshTokenService refreshTokenService,
                          JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 이메일 회원가입
    @PostMapping("/signup")
    @Operation(summary = "이메일 회원가입", description = "이메일, 회원번호를 입력받아 신규 회원을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이메일 회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "요청값이 유효하지 않습니다.",
            content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Void> signup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserSignupRequest.class))
            )
            @RequestBody UserSignupRequest request){
     
        log.info("🔥 회원가입 요청 도착: {}", request.getEmail());
        userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인을 하고 토큰을 응답받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
            content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<UserLoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserLoginRequest.class))
            )
            @RequestBody UserLoginRequest request, HttpServletResponse res){
        UserLoginResponse response = userService.login(request);

        String refresh = refreshTokenService.issue(response.getUserId());
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(java.time.Duration.ofDays(14))
                .build();
        res.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "액세스 토큰 재발급", description = "쿠키의 리프레시 토큰으로 새 액세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 만료/무효",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<Map<String, String>> refresh(
            @CookieValue(value = "refresh_token", required = false) String raw,
            HttpServletResponse res) {

        Long userId = refreshTokenService.verifyAndConsume(raw);
        if (userId == null) {
            ResponseCookie clear = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true).secure(true).path("/").sameSite("Strict")
                    .maxAge(java.time.Duration.ZERO).build();
            res.addHeader("Set-Cookie", clear.toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("code", "REFRESH_EXPIRED"));
        }

        var user = userService.getUserById(String.valueOf(userId));
        SecurityUser principal = new SecurityUser(user);
        var auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities());
        String newAccess = jwtTokenProvider.generateToken(auth);
        String newRefresh = refreshTokenService.issue(userId);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", newRefresh)
                .httpOnly(true).secure(true).path("/").sameSite("Strict")
                .maxAge(java.time.Duration.ofDays(14)).build();
        res.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }

/*    // OAuth2 로그인 URL 제공
    @GetMapping("/oauth2/url/{provider}")
    public ResponseEntity<Map<String, String>> getOAuth2LoginUrl(@PathVariable String provider) {
        String authUrl = String.format("/api/auth/oauth2/authorize/%s", provider.toLowerCase());
        return ResponseEntity.ok(Map.of("authUrl", authUrl));
    }*/
}
