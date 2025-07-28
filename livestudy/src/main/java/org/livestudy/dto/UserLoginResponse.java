package org.livestudy.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponse {
    @Schema(description = "JWT Access Token", example = "eyJhbGciOiJIUzI1...")
    private String token;
}
