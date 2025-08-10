package org.livestudy.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import org.livestudy.dto.TokenRequest;
import org.livestudy.dto.TokenResponse;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.livestudy.repository.redis.RoomRedisRepository;
import org.livestudy.security.SecurityUser;
import org.livestudy.service.livekit.LiveKitTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/livekit")
@Tag(name = "LiveKit API", description = "LiveKit 입장 토큰 발급 API")
public class LiveKitTokenController {

    private final LiveKitTokenService liveKitTokenService;

    private final RoomRedisRepository roomRedisRepository;

    @PostMapping("/token")
    @Operation(
            summary = "LiveKit 입장 토큰 발급",
            description = "현재 로그인된 사용자에게 LiveKit 입장용 accessToken을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 또는 토큰 발급 불가",
            content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<TokenResponse> generateToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "입장할 방 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TokenRequest.class))
            )
            @RequestBody TokenRequest request,
                                                       @AuthenticationPrincipal SecurityUser user) {
        Long userId = user.getUser().getId();
        String token = liveKitTokenService.generateToken(userId.toString(), request.getRoomId());

        return ResponseEntity.ok(new TokenResponse(token));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Livekit 입장 토큰 재발급 API",
            description = "토큰이 만료되거나 Livekit 사용 중인 사용자의 토큰에 이상이 생겼을 때 다시 발급 받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "LiveKit 입장용 토큰 재발급 성공",
            content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
            content = @Content(schema = @Schema(implementation = String.class)))

    })
    public ResponseEntity<TokenResponse> refreshLiveKitToken(
            @AuthenticationPrincipal SecurityUser userDetails) {

        String userId = String.valueOf(userDetails.getUser().getId());
        String roomId = roomRedisRepository.getUserRoom(userId);

        if (roomId == null) {
            throw new CustomException(ErrorCode.USER_NOT_IN_ROOM);
        }

        String token = liveKitTokenService.generateToken(userId, roomId);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
