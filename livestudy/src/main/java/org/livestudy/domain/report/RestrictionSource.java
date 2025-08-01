package org.livestudy.domain.report;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "제재 주체 ENUM")
public enum RestrictionSource {

    @Schema(description = "이용자 제재")
    REPORT,

    @Schema(description = "관리자 제재")
    ADMIN
}
