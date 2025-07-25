package org.livestudy.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

    @Schema(description = "입장할 방의 ID", example = "study-room-1")
    private String roomId;
}
