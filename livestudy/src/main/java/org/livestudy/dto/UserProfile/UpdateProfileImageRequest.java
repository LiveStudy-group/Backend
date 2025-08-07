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
@Schema(description = "유저 프로필 이미지 수정 요청 DTO")
public class UpdateProfileImageRequest {

    @NotBlank(message = "새로운 프로필 이미지 주소는 비어있을 수 없습니다.")
    @Schema(description = "새로운 프로필 이미지 주소", example = "https://newImage.com/profile.png", maxLength = 1024)
    private String newProfileImage;

}
