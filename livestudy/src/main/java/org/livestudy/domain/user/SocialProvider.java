package org.livestudy.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 타입 ENUM")
public enum SocialProvider {

    @Schema(description = "일반 로그인")
    LOCAL,

    @Schema(description = "구글 로그인")
    GOOGLE,

    @Schema(description = "카카오 로그인")
    KAKAO,

    @Schema(description = "네이버 로그인")
    NAVER
}
