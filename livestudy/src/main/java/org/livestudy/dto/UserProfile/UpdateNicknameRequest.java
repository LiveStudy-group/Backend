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
@Schema(description = "유저 닉네임 수정 요청 DTO")
public class UpdateNicknameRequest {

    @NotBlank(message = "새로운 닉네임은 비어있을 수 없습니다.")
    @Size(max = 20, message = "최대 20자까지 입력하실 수 있습니다.")
    @Schema(description = "새로운 닉네임", example = "열공이", maxLength = 20)
    private String newNickname;

}
