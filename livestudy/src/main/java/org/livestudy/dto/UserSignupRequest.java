package org.livestudy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.livestudy.domain.user.SocialProvider;

@Getter
@AllArgsConstructor
@Builder
public class UserSignupRequest {

    // 통상 이메일 로그인 시
    private String email;
    private String password;
    private String nickname;
    private String introduction;
    private String profileImage;

    // 소셜 로그인 여부 확인
    private SocialProvider socialProvider;
}
