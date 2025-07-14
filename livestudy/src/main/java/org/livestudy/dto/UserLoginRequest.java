package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "사용자 이메일 로그인 요청 DTO")
@Data
@AllArgsConstructor
public class UserLoginRequest {

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "0000")
    private String password;
}
