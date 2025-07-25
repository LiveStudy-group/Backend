package org.livestudy.service;

import org.livestudy.domain.TrackType.TrackType;
import org.livestudy.dto.TrackInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class TrackServiceImpl implements  TrackService {


    private final RedisTemplate<String, TrackInfo> redis;

    private static final String TRACK_KEY_PREFIX = "track:";

    public TrackServiceImpl(@Qualifier("TrackRedisTemplate")RedisTemplate<String, TrackInfo> redis) {
        this.redis = redis;
    }

    @Override
    public void saveTrack(String userId, String trackSid, TrackType type) {
        String trackKey = TRACK_KEY_PREFIX + trackSid;
        String userKey = TRACK_KEY_PREFIX + userId;

        TrackInfo info = TrackInfo.builder()
                        .userId(userId)
                        .trackSid(trackSid)
                        .type(type)
                        .build();

        redis.opsForValue().set(trackKey, info);
        redis.opsForHash().put(userKey, type.name(), trackSid);
    }


    @Override
    public void removeTrack(String trackSid) {
        String trackKey = "track:" + trackSid;
        String userId = Objects.requireNonNull(redis.opsForValue().get(trackKey)).toString();

        if(userId != null) {
            String userKey = "track:" + userId;
            redis.opsForHash().delete(userKey, "audio", "video", "screen_share");
        }

        redis.delete(trackKey);
    }

    @Override
    public Map<Object, Object> getTracksByUser(String userId) {
        String userKey = "track:" + userId;
        return redis.opsForHash().entries(userKey);
    }
}
