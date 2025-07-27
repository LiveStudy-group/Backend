package org.livestudy.dto.timer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livestudy.domain.studyroom.FocusStatus;

import java.time.LocalDateTime;

@Schema(description = "현재 타이머 상태 조회 응답 DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimerStatusResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "방 ID", example = "1")
    private Long roomId;

    @Schema(description = "사용자 닉네임", example = "스터디왕")
    private String nickname;

    @Schema(description = "현재 집중 상태", example = "FOCUS")
    private FocusStatus currentStatus;

    @Schema(description = "현재 세션에서의 집중 시간(초)", example = "1800")
    private Integer currentSessionStudyTime;

    @Schema(description = "현재 세션에서의 자리비움 시간(초)", example = "120")
    private Integer currentSessionAwayTime;

    @Schema(description = "총 누적 집중 시간(초)", example = "7200")
    private Integer totalStudyTime;

    @Schema(description = "총 누적 자리비움 시간(초)", example = "600")
    private Integer totalAwayTime;

    @Schema(description = "상태 변경 시각")
    private LocalDateTime statusChangedAt;

    @Schema(description = "현재 상태 지속 시간(초)", example = "900")
    private Integer currentStatusDuration;
}