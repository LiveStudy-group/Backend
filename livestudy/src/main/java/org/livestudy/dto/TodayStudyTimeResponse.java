package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "오늘 공부 시간 조회 DTO")
public class TodayStudyTimeResponse {

    @Schema(description = "오늘 공부 시간", example = "1222")
    private Integer todayStudyTime;
}
