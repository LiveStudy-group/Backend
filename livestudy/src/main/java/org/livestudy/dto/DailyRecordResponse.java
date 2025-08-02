package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyRecordResponse {

    @Schema(description = "오늘의 공부기록을 기록하는 날짜", example = "2025.08.02")
    private LocalDate recordDate;

    @Schema(description = "오늘 공부한 시간", example = "79")
    private Long dailyStudyTime;

    @Schema(description = "오늘 휴식한 시간", example = "40")
    private Long dailyAwayTime;

    @Schema(description = "오늘의 휴식시간 대비 공부시간의 비율", example = "0.78")
    private Double focusRatio;
}
