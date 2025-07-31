package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.dto.timer.TimerControlRequest;
import org.livestudy.dto.timer.TimerResponse;
import org.livestudy.dto.timer.TimerStartRequest;
import org.livestudy.dto.timer.TimerStatusResponse;
import org.livestudy.service.TimerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "집중 타이머 API", description = "집중 타이머 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/timer")
@RequiredArgsConstructor
public class TimerController {

    private final TimerService timerService;

    @Operation(summary = "집중 시작", description = "집중 타이머를 시작합니다.")
    @PostMapping("/start")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TimerResponse> startFocus(@RequestBody TimerStartRequest request) {
        log.info("집중 시작 API 호출: {}", request);
        TimerResponse response = timerService.startFocus(request.getUserId(), request.getRoomId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "집중 일시정지", description = "집중 타이머를 일시정지합니다. (자리비움)")
    @PostMapping("/pause")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TimerResponse> pauseFocus(@RequestBody TimerControlRequest request) {
        log.info("집중 일시정지 API 호출: {}", request);
        TimerResponse response = timerService.pauseFocus(request.getUserId(), request.getRoomId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "집중 재개", description = "일시정지된 집중 타이머를 재개합니다.")
    @PostMapping("/resume")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TimerResponse> resumeFocus(@RequestBody TimerControlRequest request) {
        log.info("집중 재개 API 호출: {}", request);
        TimerResponse response = timerService.resumeFocus(request.getUserId(), request.getRoomId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "집중 종료", description = "집중 타이머를 종료하고 방에서 퇴장합니다.")
    @PostMapping("/stop")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TimerResponse> stopFocus(@RequestBody TimerControlRequest request) {
        log.info("집중 종료 API 호출: {}", request);
        TimerResponse response = timerService.stopFocus(request.getUserId(), request.getRoomId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "타이머 상태 조회", description = "현재 사용자의 타이머 상태를 조회합니다.")
    @GetMapping("/status/{userId}/{roomId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TimerStatusResponse> getTimerStatus(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "방 ID") @PathVariable Long roomId) {
        log.info("타이머 상태 조회 API 호출: userId={}, roomId={}", userId, roomId);
        TimerStatusResponse response = timerService.getTimerStatus(userId, roomId);
        return ResponseEntity.ok(response);
    }
}