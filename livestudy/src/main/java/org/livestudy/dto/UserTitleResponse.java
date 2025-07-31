package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "대표 칭호 장착 응답 DTO")
public class UserTitleResponse {

    private Long titleId;
    private String name;
    private String description;
    private boolean isRepresentative;
}
