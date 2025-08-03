package org.livestudy.dto.UserProfile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "유저 이메일 수정 요청 DTO")
public class UpdateEmailRequest {

    @NotBlank(message = "새로운 이메일은 비어있을 수 없습니다.")
    @Size(max = 100, message = "최대 100자까지 입력하실 수 있습니다.")
    @Schema(description = "새로운 이메일 주소", example = "new@example.com", maxLength = 100)
    private String newEmail;

}
