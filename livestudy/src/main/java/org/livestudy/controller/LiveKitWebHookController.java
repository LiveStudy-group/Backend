package org.livestudy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.livestudy.domain.TrackType.TrackType;
import org.livestudy.dto.LiveKitWebHookEvent;
import org.livestudy.service.TrackServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook/livekit")
@RequiredArgsConstructor
@Tag(name = "WebHook API",
        description = "LiveKit 서버에서 전송하는 WebHook 이벤트를 처리합니다.\n" +
                "- 이 API는 외부 사용자 대상이 아니며, LiveKit 서버에 의해 자동으로 호출되는 방식입니다.\n"
                + "각 이벤트에 따라 트랙 정보를 저장하거나 제거합니다.")
public class LiveKitWebHookController {

    private final TrackServiceImpl trackService;

    @PostMapping
    @Operation(
            summary = "LiveKit WebHook 이벤트 수신",
            description = "LiveKit 서버가 `track_published`, `track_unpublished` 이벤트 발생 시 호출하는 Webhook API입니다.\n" +
                    "- `track_published`: 사용자의 트랙 정보 저장\n" +
                    "- `track_unpublished`: 트랙 정보 제거\n" +
                    "**주의: 이 API는 인증 없이 호출됩니다. 외부에서 직접 호출하지 마세요.**"
    )
    public ResponseEntity<Void> handleWebHook(@RequestBody LiveKitWebHookEvent event) {
        String eventType = event.getEvent();

        if("track_published".equals(eventType)) {
            String userId = event.getParticipant().getId();
            String trackSid = event.getTrack().getSid();
            TrackType type = TrackType.valueOf(String.valueOf(event.getTrack().getType()));

            trackService.saveTrack(userId, trackSid, type);
        }

        if("track_unpublished".equals(eventType)) {
            String trackSid = event.getTrack().getSid();
            trackService.removeAllTracks(trackSid);
        }

        return ResponseEntity.ok().build();
    }
}
