package org.livestudy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.livestudy.domain.TrackType.TrackType;
import org.livestudy.dto.TrackInfo;
import org.livestudy.exception.CustomException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrackServiceImplTest {

    @Mock
    private RedisTemplate<String, TrackInfo> redisTemplate;

    @Mock
    private ValueOperations<String, TrackInfo> valueOps;

    @Mock
    private HashOperations<String, Object, Object> hashOps;

    @InjectMocks
    private TrackServiceImpl trackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOps);
    }

    @Test
    void saveTrack_success() {
        String userId = "user123";
        String trackSid = UUID.randomUUID().toString();
        TrackType type = TrackType.VIDEO;

        trackService.saveTrack(userId, trackSid, type);

        verify(valueOps).set(eq("track:" + trackSid), any(TrackInfo.class));
        verify(hashOps).put("track:" + userId, type.name(), trackSid);
    }

    @Test
    void removeTrack_success(){
        String trackSid = "track1223";
        TrackType type = TrackType.SCREEN;
        TrackInfo dummyInfo = TrackInfo.builder()
                .userId("user123")
                .trackSid(trackSid)
                .type(type)
                .build();

        when(valueOps.get("track:" + trackSid)).thenReturn(dummyInfo);

        trackService.removeTrack(trackSid, type);

        verify(valueOps).get("track:" + trackSid);                     // 1. 트랙 정보 조회
        verify(hashOps).delete("track:" + "user123", type.name());        // 2. 사용자 해시에서 삭제
        verify(redisTemplate).delete("track:" + trackSid);             // 3. 트랙 키 자체 삭제

    }

    @Test
    void getTracksByUser_success() {
        String userId = "user123";
        Map<Object, Object> mockResult = Map.of("VIDEO", "track1", "AUDIO", "track2");

        when(hashOps.entries("track:" + userId)).thenReturn(mockResult);

        Map<Object, Object> result = trackService.getTracksByUser(userId);

        assertEquals(2, result.size());
        assertEquals("track1", result.get("VIDEO"));
    }

    @Test  // 기존 트랙을 삭제 후 새 트랙을 저장한다.
    void switchTrack_success() {
        String userId = "user1";
        String oldTrack = "oldTrackSid";
        String newTrack = "newTrackSid";
        TrackType type = TrackType.AUDIO;

        when(hashOps.get("track:" + userId, type.name())).thenReturn(oldTrack);
        when(valueOps.get("track:" + oldTrack)).thenReturn(
                TrackInfo.builder()
                        .userId(userId)
                        .trackSid(oldTrack)
                        .type(type)
                        .build()
        );

        trackService.switchTrack(userId, newTrack, type);

        verify(redisTemplate).delete("track:" + oldTrack);
        verify(hashOps).delete("track:" + userId, type.name());
        verify(valueOps).set(eq("track:" + newTrack), any(TrackInfo.class));

    }

    @Test
    void removeAllTracks_success() {
        String userId = "user123";

        Map<Object, Object> dummyTracks = Map.of("VIDEO", "track1", "AUDIO", "track2");

        when(hashOps.entries("track:" + userId)).thenReturn(dummyTracks);

        trackService.removeAllTracks(userId);

        verify(redisTemplate).delete("track:user123");
        verify(redisTemplate).delete("track:AUDIO");
        verify(redisTemplate).delete("track:VIDEO");

    }

    @Test
    void getTrack_whenNotExist_shouldReturnNull() {
        when(redisTemplate.opsForHash().get(anyString(), anyString())).thenReturn(null);

        String result = trackService.getTrack("nonexistent-user", TrackType.VIDEO);

        assertThat(result).isNull();
    }

    @Test
    void removeTrack_whenTrackNotExist_shouldNotThrow() {
        when(redisTemplate.delete("track:user123")).thenReturn(true);

        assertThatCode(() -> trackService.removeTrack("nonexistent-sid", TrackType.AUDIO))
                .doesNotThrowAnyException();
    }

    @Test
    void saveTrack_whenUserIdIsNull_shouldThrowException() {
        assertThatThrownBy(() -> trackService.saveTrack(null, "sid", TrackType.VIDEO))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void saveTrack_whenTrackSidIsNull_shouldThrowException() {
        assertThatThrownBy(() -> trackService.saveTrack("user", null, TrackType.VIDEO))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void saveTrack_whenTrackTypeIsNull_shouldThrowException() {
        assertThatThrownBy(() -> trackService.saveTrack("user", "sid", null))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void switchTrack_whenOldTrackNotFound_shouldSaveWithoutDeleting() {
        when(redisTemplate.opsForHash().get(anyString(), eq("VIDEO"))).thenReturn(null);

        // delete는 호출되지 않고 put만 호출
        trackService.switchTrack("user", "newSid", TrackType.VIDEO);

        verify(redisTemplate, never()).delete("track:null");
        verify(redisTemplate.opsForHash()).put("track:user", "VIDEO", "newSid");
    }

    @Test
    void getAllTracks_whenNoneExist_shouldReturnEmptyMap() {
        when(redisTemplate.opsForHash().entries(anyString())).thenReturn(Map.of());

        Map<String, String> result = trackService.getAllTracks("no-track-user");

        assertThat(result).isEmpty();
    }
}
