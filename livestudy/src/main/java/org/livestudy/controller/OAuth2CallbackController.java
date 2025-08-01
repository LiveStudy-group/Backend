/*
package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.user.SocialProvider;
import org.livestudy.domain.user.User;
import org.livestudy.dto.SocialLoginResponse;
import org.livestudy.security.SecurityUser;
import org.livestudy.service.SocialLoginService;
import org.livestudy.service.UserService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@Tag(name = "소셜 로그인 콜백 API", description = "OAuth2 콜백 처리")
public class OAuth2CallbackController {

    private final UserService userService;
    private final SocialLoginService socialLoginService;
    private final RestTemplate restTemplate;

    public OAuth2CallbackController(UserService userService, SocialLoginService socialLoginService, RestTemplate restTemplate) { // <-- RestTemplate 주입받도록 생성자 변경!
        this.userService = userService;
        this.socialLoginService = socialLoginService;
        this.restTemplate = restTemplate;
    }

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${app.frontend.success-redirect-uri}")
    private String frontendSuccessRedirectUri;


    @GetMapping("/api/auth/oauth2/callback/google")
    @Operation(summary = "Google OAuth2 콜백 처리",
            description = "Google OAuth2 인증 후 리디렉션된 요청을 처리하고, 사용자 정보를 기반으로 로그인/회원가입을 진행한 후 프론트엔드로 최종 리디렉션합니다.")
    @Parameters({
            @Parameter(name = "code", description = "Google OAuth2 인증 서버로부터 받은 인가 코드"),
            @Parameter(name = "state", description = "OAuth2 흐름 중 CSRF 공격 방지를 위해 사용되는 상태 값 (선택 사항)", required = false)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "인증 성공 또는 실패 후 프론트엔드 URI로 리디렉션됩니다.",
                    headers = @Header(name = "Location", description = "리디렉션될 프론트엔드 URL (성공 시: token, email, isNewUser 포함 / 실패 시: error 포함)"))
    })
    public void googleOAuth2Callback(@RequestParam("code") String code,
                                     @RequestParam(value = "state", required = false) String state,
                                     HttpServletResponse response) throws IOException {

        String tokenUri = "https://oauth2.googleapis.com/token";
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", googleClientId);
        params.put("client_secret", googleClientSecret);
        params.put("redirect_uri", googleRedirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<Map> tokenResponse = restTemplate
                .postForEntity(tokenUri, params, Map.class);
        if (tokenResponse.getStatusCode() != HttpStatus.OK ||
                tokenResponse.getBody() == null) {
            response.sendRedirect(frontendSuccessRedirectUri +
                    "?error=token_exchange_failed");
            return;
        }
        String accessToken = (String) tokenResponse.getBody().get("access_token");

        String userInfoUri = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(
                UriComponentsBuilder.fromUriString(userInfoUri)
                        .queryParam("access_token", accessToken)
                        .build().toUriString(),
                Map.class
        );
        if (userInfoResponse.getStatusCode() != HttpStatus.OK || userInfoResponse.getBody() == null) {
            response.sendRedirect(frontendSuccessRedirectUri + "?error=user_info_fetch_failed");
            return;
        }
        Map<String, Object> userData = userInfoResponse.getBody();
        String userEmail = (String) userData.get("email");
        String userNickname = (String) userData.get("name");

        User user = userService.findOrCreateSocialUser(userEmail, userNickname, SocialProvider.valueOf("GOOGLE"));

        // 신규 가입자는 true
        boolean isNewUser = user.isNewUser();

        SecurityUser securityUser = new SecurityUser(user, userData);
        SocialLoginResponse socialLoginResponse = socialLoginService.createSocialLoginResponse(securityUser, isNewUser);

        String appAuthToken = socialLoginResponse.getToken();

        // FE로 리디렉션
        String finalRedirectUrl = UriComponentsBuilder.fromUriString(frontendSuccessRedirectUri)
                .queryParam("token", appAuthToken)
                .queryParam("email", userEmail)
                .queryParam("isNewUser", isNewUser)
                .build().encode().toUriString();

        response.sendRedirect(finalRedirectUrl);
    }
}*/
