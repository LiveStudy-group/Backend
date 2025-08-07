package org.livestudy.domain.report;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신고 종류(타입) ENUM")
public enum RestrictionType {

    @Schema(description = "일시 정지")
    TEMPORARY,

    @Schema(description = "영구 정지")
    PERMANENT
}
