package org.livestudy.domain.report;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "신고 사유 ENUM")
public enum ReportReason {

    @Schema(description = "음란물")
    OBSCENE_CONTENT,

    @Schema(description = "욕설")
    ABUSE,

    @Schema(description = "방해")
    DISTURBANCE
}