package org.livestudy.domain.studyroom;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "집중 타입 ENUM")
public enum FocusStatus {

    @Schema(description = "공부 집중 상태")
    FOCUS,

    @Schema(description = "자리비움 상태")
    AWAY
}
