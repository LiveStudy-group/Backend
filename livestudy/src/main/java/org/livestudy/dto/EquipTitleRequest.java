package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipTitleRequest {

    @Schema(description = "사용자 ID", example = "user123")
    private Long userId;

    @Schema(description = "장착할 칭호 ID", example = "10")
    private Long titleId;


}
