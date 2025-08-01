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
@Schema(description = "유저 비밀번호 수정 요청 DTO")
public class UpdatePasswordRequest {

    @NotBlank(message = "현재 비밀번호는 비어있을 수 없습니다.")
    @Size(max = 70, message = "최대 70자까지 입력하실 수 있습니다.")
    @Schema(description = "현재 사용 중인 비밀번호", example = "pass123", maxLength = 70)
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호는 비어있을 수 없습니다.")
    @Size(max = 70, message = "최대 70자까지 입력하실 수 있습니다.")
    @Schema(description = "새로운 비밀번호", example = "word456", maxLength = 70)
    private String newPassword;

    @NotBlank(message = "새로운 비밀번호는 비어있을 수 없습니다.")
    @Size(max = 70, message = "최대 70자까지 입력하실 수 있습니다.")
    @Schema(description = "새로운 비밀번호 확인", example = "word456", maxLength = 70)
    private String confirmNewPassword;

}
