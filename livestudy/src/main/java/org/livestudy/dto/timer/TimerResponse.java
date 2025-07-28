package org.livestudy.dto.timer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livestudy.domain.studyroom.FocusStatus;

import java.time.LocalDateTime;

@Schema(description = "타이머 응답 DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimerResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "방 ID", example = "1")
    private Long roomId;

    @Schema(description = "현재 집중 상태", example = "FOCUS")
    private FocusStatus status;

    @Schema(description = "총 집중 시간(초)", example = "3600")
    private Integer totalStudyTime;

    @Schema(description = "총 자리비움 시간(초)", example = "300")
    private Integer totalAwayTime;

    @Schema(description = "상태 변경 시각")
    private LocalDateTime statusChangedAt;

    @Schema(description = "방 입장 시각")
    private LocalDateTime joinTime;
}