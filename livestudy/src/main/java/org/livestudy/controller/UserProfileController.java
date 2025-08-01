package org.livestudy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.dto.UserProfile.*;
import org.livestudy.security.SecurityUser;
import org.livestudy.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
@CrossOrigin(origins = "https://live-study.com", methods = {RequestMethod.GET, RequestMethod.PATCH})
public class UserProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @AuthenticationPrincipal SecurityUser user) {
        Long userId = user.getUser().getId();
        log.info("마이페이지 - userId: {} 유저의 프로필 조회", userId);

        UserProfileResponse userProfile = profileService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateNickname(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdateNicknameRequest request) {

        Long userId = user.getUser().getId();

        profileService.updateNickname(userId, request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdateProfileImageRequest request) {

        Long userId = user.getUser().getId();

        profileService.updateProfileImage(userId, request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/email")
    public ResponseEntity<Void> updateEmail(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdateEmailRequest request) {

        Long userId = user.getUser().getId();

        profileService.updateEmail(userId, request);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdatePasswordRequest request) {

        Long userId = user.getUser().getId();

        profileService.updatePassword(userId, request);

        return ResponseEntity.ok().build();
    }


}