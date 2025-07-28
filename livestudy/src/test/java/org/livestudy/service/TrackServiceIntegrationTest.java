package org.livestudy.service;


import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.livestudy.config.RedisConfig;
import org.livestudy.domain.TrackType.TrackType;
import org.livestudy.dto.TrackInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Import(RedisConfig.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TrackServiceIntegrationTest {

    @Autowired
    private TrackService trackService;

    @Autowired
    private RedisTemplate<String, TrackInfo> redisTemplate;

    private final String userId = "test-user";
    private final String videoTrackSid = "video-sid-123";
    private final String audioTrackSid = "audio-sid-123";

    @Test
    @Order(1)
    void saveTrack_thenGetTrack_shouldReturnCorrectValue(){
        trackService.saveTrack(userId, videoTrackSid, TrackType.VIDEO);
        String saveSid = trackService.getTrack(userId, TrackType.VIDEO);

        assertThat(saveSid).isEqualTo(videoTrackSid);
    }

    @Test
    @Order(2)
    void saveMultipleTracks_thenGetAllTracks_shouldReturnAll() {
        trackService.saveTrack(userId, audioTrackSid, TrackType.AUDIO);

        Map<String, String> allTracks = trackService.getAllTracks(userId);

        assertThat(allTracks).containsEntry("VIDEO", videoTrackSid);
        assertThat(allTracks).containsEntry("AUDIO", audioTrackSid);
    }

    @Test
    @Order(3)
    void switchTrack_shouldReplaceOldTrack() {
        String newVideoSid = "video-sid-789";

        trackService.switchTrack(userId, newVideoSid, TrackType.VIDEO);
        String result = trackService.getTrack(userId, TrackType.VIDEO);

        assertThat(result).isEqualTo(newVideoSid);
        assertThat(redisTemplate.opsForValue().get("track:" + videoTrackSid)).isNull(); // 이전 track은 삭제됨
    }

    @Test
    @Order(4)
    void removeTrack_shouldDeleteFromRedis() {
        trackService.removeTrack(audioTrackSid, TrackType.AUDIO);

        String result = trackService.getTrack(userId, TrackType.AUDIO);
        assertThat(result).isNull();
    }

    @Test
    @Order(5)
    void removeAllTracks_shouldCompletelyDeleteUser() {
        trackService.removeAllTracks(userId);

        Map<Object, Object> result = trackService.getTracksByUser(userId);
        assertThat(result).isEmpty();
    }
}
