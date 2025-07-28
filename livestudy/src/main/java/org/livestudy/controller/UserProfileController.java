package org.livestudy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.dto.UserStudyStatsResponse;
import org.livestudy.security.SecurityUser;
import org.livestudy.service.UserStudyStatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/profile")
public class UserProfileController {

    private final UserStudyStatService userStudyStatService;

    // 누적 공부시간
    @GetMapping
    public ResponseEntity<UserStudyStatsResponse> getTotalStudyTime(
            @AuthenticationPrincipal SecurityUser user) {

        Long userId = user.getUser().getId();
        log.info("마이페이지 - userId: {} 유저의 총 공부시간", userId);
        UserStudyStatsResponse statsResponse = userStudyStatService
                .getUserStudyStats(userId);

        return ResponseEntity.ok(statsResponse);
    }

}
