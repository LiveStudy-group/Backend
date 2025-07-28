package org.livestudy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livestudy.domain.user.SocialProvider;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String nickname;
    private String profileImage;
    private SocialProvider socialProvider;
    private boolean isNewUser; // 신규 가입자인지 여부
}