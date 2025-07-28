package org.livestudy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.dto.DailyRecordResponse;
import org.livestudy.dto.UserStudyStatsResponse;
import org.livestudy.security.SecurityUser;
import org.livestudy.service.UserStudyStatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/stat")
public class UserStatController {

    private final UserStudyStatService userStudyStatService;

    // 기본 데이터
    @GetMapping("/normal")
    public ResponseEntity<UserStudyStatsResponse> getUserNormalStats(
            @AuthenticationPrincipal SecurityUser user) {

        Long userId = user.getUser().getId();

        log.info("통계 페이지 - userId: {} 유저의 공부 정보 조회", userId);
        UserStudyStatsResponse statsResponse = userStudyStatService
                .getUserStudyStats(userId);

        return ResponseEntity.ok(statsResponse);
    }

    @GetMapping("/daily-focus")
    public ResponseEntity<List<DailyRecordResponse>> getDailyFocus(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Long userId = user.getUser().getId();

        LocalDate end = (endDate != null) ? endDate : LocalDate.now();
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(6);

        log.info("통계 페이지 - userId: {} 유저의 일별 집중도 추이 조회", userId);
        List<DailyRecordResponse> dailyFocus = userStudyStatService.getDailyRecord(
                userId, start, end);

        return ResponseEntity.ok(dailyFocus);
    }

    @GetMapping("/average-focus-ratio")
    public ResponseEntity<Double> getAverageFocusRatio(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Long userId = user.getUser().getId();

        LocalDate end = (endDate != null) ? endDate : LocalDate.now();
        LocalDate start = (startDate != null) ? startDate : LocalDate.now().minusDays(6);

        log.info("통계 페이지 - userId: {} 유저의 기간별 집중률 조회", userId);
        Double averageFocusRatio = userStudyStatService.getAverageStudyRatio(userId, start, end);

        return ResponseEntity.ok(averageFocusRatio);
    }

}
