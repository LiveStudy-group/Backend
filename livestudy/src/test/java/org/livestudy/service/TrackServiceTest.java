package org.livestudy.service;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.livestudy.domain.TrackType.TrackType;
import org.livestudy.dto.TrackInfo;
import org.livestudy.repository.redis.RoomRedisRepository;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TrackServiceTest {

    @Autowired
    private TrackService trackService;

    @Autowired
    @Qualifier("TrackRedisTemplate")
    private RedisTemplate<String, TrackInfo> redisTemplate;



    private final String userId = "user123";
    private final String trackSid = "trackABC";
    private final TrackType type = TrackType.VIDEO;

    private final String trackKey = "track:" + trackSid;

    @Autowired
    private RoomRedisRepository roomRedisRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);  // 꼭 필요!
    }

    @AfterEach
    void tearDown() throws Exception {
        redisTemplate.delete(trackKey);
        mocks.close();
    }

    @Test
    @DisplayName("트랙 정보를 저장하고 다시 조회할 수 있다")
    void saveAndGetTrack() {
        TrackInfo expected = TrackInfo.builder()
                .userId(userId)
                .trackSid(trackSid)
                .type(type)
                .build();

        // 저장
        trackService.saveTrack(userId, trackSid, type);

        // 조회
        TrackInfo result = redisTemplate.opsForValue().get(trackKey);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("트랙 정보를 삭제하면 조회 결과가 null이어야 한다")
    void removeTrack() {
        trackService.saveTrack(userId, trackSid, type);

        trackService.removeAllTracks(trackSid);

        TrackInfo result = redisTemplate.opsForValue().get(trackKey);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 유저 ID로 방 조회 시")
    void getUserRoom_shouldReturnNull_whenUserDoesNotExist() {
        // given
        String userId = "nonexistUser";

        // when
        String room = roomRedisRepository.getUserRoom(userId);

        // then
        assertNull(room);
    }


    @Test
    @DisplayName("잘못된 숫자값이 들어가 있어서 증가할 수 없는 경우")
    void incrementRoomCount_shouldThrow_whenValueIsNotNumber(){
        // given
        String roomId = "room123";
        stringRedisTemplate.opsForValue().set("room:" + roomId + ":count", "NotANumber");

        // when & then


        assertThrows(RedisSystemException.class, () -> {
            roomRedisRepository.incrementRoomCount(roomId);
        });
    }
}
