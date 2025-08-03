package org.livestudy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "대표 칭호 장착 응답 DTO")
public class UserTitleResponse {

    @Schema(description = "타이틀 고유 식별자")
    private Long titleId;

    @Schema(description = "칭호 이름")
    private String name;

    @Schema(description = "칭호 내용")
    private String description;

    @Schema(description = "대표 칭호 여부")
    @JsonProperty("isRepresentative")
    private boolean isRepresentative;
}
