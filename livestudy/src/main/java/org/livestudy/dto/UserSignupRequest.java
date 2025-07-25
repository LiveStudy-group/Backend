package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.livestudy.domain.user.SocialProvider;

@Getter
@AllArgsConstructor
@Builder
public class UserSignupRequest {

    // 통상 이메일 로그인 시
    @Schema(description = "이메일 주소", example = "test@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "123$$$!")
    private String password;

    @Schema(description = "사용자 닉네임", example = "델키란")
    private String nickname;

    @Schema(description = "자기 소개 문구", example = "잘 부탁 드립니다.")
    private String introduction;

    @Schema(description = "프로필 이미지", example = "profile.image")
    private String profileImage;

    // 소셜 로그인 여부 확인
    @Schema(description = "소셜 로그인 확인", example = "SocialProvider.LOCAL")
    private SocialProvider socialProvider;
}
