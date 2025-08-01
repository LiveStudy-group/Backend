package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "평군 집중률 응답 DTO")
public class AverageFocusRatioResponse {

    @Schema(description = "조회 시작일", example = "2025-07-22")
    private LocalDate startDate;

    @Schema(description = "조회 종료일", example = "2025-07-29")
    private LocalDate endDate;

    @Schema(description = "해당 기간의 평균 집중률", example = "0.34")
    private Double averageFocusRatio;
}
