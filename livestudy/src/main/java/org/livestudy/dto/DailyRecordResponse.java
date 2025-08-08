package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "하루 기록 응답 DTO")
public class DailyRecordResponse {

    @Schema(description = "기록한 날짜", example = "2025-08-01")
    private LocalDate recordDate;

    @Schema(description = "하루 공부 시간", example = "2222")
    private Integer dailyStudyTime;

    @Schema(description = "하루 자리 비움 시간", example = "1111")
    private Integer dailyAwayTime;

    @Schema(description = "하루 집중률", example = "0.53")
    private Double focusRatio;
}
