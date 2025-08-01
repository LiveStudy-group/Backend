package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.dto.ErrorResponse;
import org.livestudy.dto.UserProfile.*;
import org.livestudy.dto.report.ReportRequest;
import org.livestudy.security.SecurityUser;
import org.livestudy.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
@Tag(name = "마이페이지 API", description = "마이페이지 데이터 조회/수정 API")
public class UserProfileController {

    private final ProfileService profileService;

    @GetMapping
    @Operation(summary = "프로필 조회", description = "유저(자신)에 대한 프로필 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필이 성공적으로 조회되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "조회 대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생.")
    })
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @AuthenticationPrincipal SecurityUser user) {
        Long userId = user.getUser().getId();
        log.info("마이페이지 - userId: {} 유저의 프로필 조회", userId);

        UserProfileResponse userProfile = profileService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    @PatchMapping("/nickname")
    @Operation(summary = "닉네임 수정", description = "유저(자신)의 닉네임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "닉네임 수정 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateNicknameRequest.class))
    )
    public ResponseEntity<Void> updateNickname(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdateNicknameRequest request) {

        Long userId = user.getUser().getId();

        profileService.updateNickname(userId, request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/profileImage")
    @Operation(summary = "프로필이미지 수정", description = "유저(자신)의 프로필 이미지를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지가 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "프로필이미지 수정 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateProfileImageRequest.class))
    )
    public ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdateProfileImageRequest request) {

        Long userId = user.getUser().getId();

        profileService.updateProfileImage(userId, request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/email")
    @Operation(summary = "이메일 수정", description = "유저(자신)의 이메일을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일이 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "이메일 수정 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateEmailRequest.class))
    )
    public ResponseEntity<Void> updateEmail(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdateEmailRequest request) {

        Long userId = user.getUser().getId();

        profileService.updateEmail(userId, request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    @Operation(summary = "비밀번호 수정", description = "유저(자신)의 비밀번호를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 성공적으로 수정되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "수정할 대상을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생.")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "비밀번호 수정 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdatePasswordRequest.class))
    )
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdatePasswordRequest request) {

        Long userId = user.getUser().getId();

        profileService.updatePassword(userId, request);

        return ResponseEntity.ok().build();
    }


}