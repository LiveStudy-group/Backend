package org.livestudy.repository.redis;

import lombok.RequiredArgsConstructor;
import org.livestudy.dto.TrackInfo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TrackRedisRepository {


    private final RedisTemplate<String, String> stringRedisTemplate; // trackId 저장용
    private final RedisTemplate<String, TrackInfo> trackInfoRedisTemplate; // TrackInfo 저장용

    private static final String TRACK_ID_PREFIX = "track:id:";
    private static final String TRACK_INFO_PREFIX = "track:info:";

    // trackId 저장
    public void saveTrackId(String userId, String trackId) {
        stringRedisTemplate.opsForValue().set(TRACK_ID_PREFIX + userId, trackId);
    }

    // trackId 조회
    public String getTrackId(String userId) {
        return stringRedisTemplate.opsForValue().get(TRACK_ID_PREFIX + userId);
    }

    // trackId 삭제
    public void deleteTrackId(String userId) {
        stringRedisTemplate.delete(TRACK_ID_PREFIX + userId);
    }

    // TrackInfo 저장
    public void saveTrackInfo(String userId, TrackInfo info) {
        trackInfoRedisTemplate.opsForValue().set(TRACK_INFO_PREFIX + userId, info);
    }

    // TrackInfo 조회
    public TrackInfo getTrackInfo(String userId) {
        return trackInfoRedisTemplate.opsForValue().get(TRACK_INFO_PREFIX + userId);
    }

    // TrackInfo 삭제
    public void deleteTrackInfo(String userId) {
        trackInfoRedisTemplate.delete(TRACK_INFO_PREFIX + userId);
    }
}
