package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livestudy.domain.user.SocialProvider;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "소셜 로그인 응답 DTO")
public class SocialLoginResponse {

    @Schema(description = "발급 받은 토큰")
    private String token;

    @Schema(description = "유저 ID", example = "1L")
    private Long userId;

    @Schema(description = "유저 이메일", example = "new@example.com")
    private String email;

    @Schema(description = "유저 닉네임", example = "열공이")
    private String nickname;

    @Schema(description = "유저 프로필 이미지 주소", example = "https://newImage.com/profile.png")
    private String profileImage;

    @Schema(description = "소셜 로그인 종류", example = "GOOGLE")
    private SocialProvider socialProvider;

    @Schema(description = "신규 회원 여부", example = "true")
    private boolean isNewUser; // 신규 가입자인지 여부
}