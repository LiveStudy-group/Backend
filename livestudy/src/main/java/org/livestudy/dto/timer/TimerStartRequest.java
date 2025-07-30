package org.livestudy.dto.timer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "타이머 시작 요청 DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimerStartRequest {

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "방 ID", example = "1")
    private Long roomId;
}