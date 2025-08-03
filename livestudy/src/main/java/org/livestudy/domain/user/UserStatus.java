package org.livestudy.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 상태 ENUM")
public enum UserStatus {

    @Schema(description = "일반")
    NORMAL, // 평상시

    @Schema(description = "일시 정지")
    TEMPORARY_BAN,  // 정지 당할 시 Status

    @Schema(description = "영구 정지")
    PERMANENT_BAN  // 영구 삭제 당할 시 Status
}
