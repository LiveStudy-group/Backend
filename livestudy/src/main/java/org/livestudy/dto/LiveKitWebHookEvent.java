package org.livestudy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.livestudy.domain.TrackType.TrackType;

@Data
@Schema(description = "LiveKit Webhook 이벤트 객체")
public class LiveKitWebHookEvent {

    @Schema(description = "이벤트 타입 (예: track_published, track_unpublished)", example = "track_published")
    private String event;

    @Schema(description = "참여자 정보")
    private Participant participant;

    @Schema(description = "트랙 정보")
    private Track track;

    @Data
    @Schema(description = "이벤트를 발생시킨 참여자 정보")
    public static class Participant {

        @Schema(description = "참여자 ID (userId)", example = "user-1234")
        private String id;
    }

    @Data
    @Schema(description = "발행된 트랙 정보")
    public static class Track{

        @Schema(description = "트랙의 고유 ID", example = "TR_abcdef123456")
        private String sid;  // 트랙 ID

        @Schema(description = "트랙 타입", example = "video")
        private TrackType type; // "audio" / "video" / "screen_share"
    }
}
