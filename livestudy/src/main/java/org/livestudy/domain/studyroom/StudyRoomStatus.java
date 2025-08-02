package org.livestudy.domain.studyroom;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스터디룸 상태 ENUM")
public enum StudyRoomStatus {

    @Schema(description = "열림(입장 가능)")
    OPEN,

    @Schema(description = "열림(인원 수 초과로 입장 불가)")
    FULL,

    @Schema(description = "닫힘")
    CLOSE
}
