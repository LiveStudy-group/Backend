package org.livestudy.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    @Schema(description = "발급된 LiveKit accessToken", example = "eyJhbGciOiJIUzI1...")
    private String token;
}
