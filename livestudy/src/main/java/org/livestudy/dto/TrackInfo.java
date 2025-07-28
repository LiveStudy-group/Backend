package org.livestudy.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.livestudy.domain.TrackType.TrackType;
import org.livestudy.domain.badge.Badge;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자의 Track 정보")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class")
public class TrackInfo implements Serializable {

    @Schema(description = "사용자 ID", example = "user-1")
    private String userId;

    @Schema(description = "트랙의 고유 식별자", example = "TR_abcdef123456")
    private String trackSid;

    @Schema(description = "참여 중인 방의 ID", example = "study_room_1")
    private String roomId;

    @Schema(description = "트랙 타입", example = "video")
    private TrackType type;

    @Builder.Default
    @Schema(description = "사용자에게 적용 중인 배지", example = "NONE")
    private Badge badgeType = Badge.NONE; // 기본값은 NONE으로 설정


}
