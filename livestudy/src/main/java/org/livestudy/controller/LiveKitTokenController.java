package org.livestudy.controller;

import lombok.RequiredArgsConstructor;
import org.livestudy.dto.TokenRequest;
import org.livestudy.dto.TokenResponse;
import org.livestudy.security.SecurityUser;
import org.livestudy.service.LiveKitTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/livekit")
public class LiveKitTokenController {

    private final LiveKitTokenService liveKitTokenService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> generateToken(@RequestBody TokenRequest request,
                                                       @AuthenticationPrincipal SecurityUser user) {
        Long userId = user.getUser().getId();

        String token = liveKitTokenService.generateToken(request.getRoomId(), userId.toString());

        return ResponseEntity.ok(new TokenResponse(token));
    }
}
