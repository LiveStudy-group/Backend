package org.livestudy.service;

import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.livestudy.domain.TrackType.TrackType;
import org.livestudy.dto.TrackInfo;
import org.livestudy.exception.CustomException;
import org.livestudy.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
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

        // TrackType null 체크
        if(info.getType() == null){
            throw new CustomException(ErrorCode.TRACK_TYPE_SHOULD_NOT_BE_NULL);
        }

        // TrackSid null 체크
        if(info.getTrackSid() == null){
            throw new CustomException(ErrorCode.TRACK_SID_SHOULD_NOT_BE_NULL);
        }

        // userId null 체크
        if(info.getUserId() == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }


        try {
            redis.opsForValue().set(trackKey, info);
            redis.opsForHash().put(userKey, type.name(), trackSid);
        } catch (Exception e) {
            log.error("❌ Redis 저장 실패: {}", e.getMessage(), e);
            throw new RedisConnectionException("Redis에 Track 정보를 저장할 수 없습니다.");
        }
    }


    @Override
    public void removeTrack(String trackSid, TrackType type) {
        String trackKey = TRACK_KEY_PREFIX + trackSid;


        try {
            TrackInfo info = redis.opsForValue().get(trackKey);
            if (info != null) {
                String userKey = TRACK_KEY_PREFIX + info.getUserId();
                redis.opsForHash().delete(userKey, info.getType().name());
            }
            redis.delete(trackKey);
        } catch (Exception e) {
            log.error("❌ Redis 삭제 실패: {}", e.getMessage(), e);
            throw new RedisConnectionException("Redis에서 Track 정보를 삭제할 수 없습니다.");
        }
    }

    @Override
    public Map<Object, Object> getTracksByUser(String userId) {
        String userKey = TRACK_KEY_PREFIX + userId;

        try {
            return redis.opsForHash().entries(userKey);
        } catch (Exception e) {
            log.error("❌ Redis 조회 실패: {}", e.getMessage(), e);
            throw new RedisConnectionException("Redis에서 Track 정보를 조회할 수 없습니다.");
        }
    }

    @Override
    public void switchTrack(String userId, String newTrackSid, TrackType type) {
        String oldTracksid = getTrack(userId, type);
        if(oldTracksid != null){
            removeTrack(oldTracksid, type);
        }

        // 새 트랙 저장
        saveTrack(userId, newTrackSid, type);
    }

    @Override
    public String getTrack(String userId, TrackType type) {
        String userKey = TRACK_KEY_PREFIX + userId;

        try{
            Object result = redis.opsForHash().get(userKey, type.name());
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            log.error("Redis Get Tracking 실패 : {}", e.getMessage(), e);
            throw new RedisConnectionException("Redis에서 Track 정보를 조회할 수 없습니다.");
        }
    }

    @Override
    public Map<String, String> getAllTracks(String userId) {
        String userKey =  TRACK_KEY_PREFIX + userId;

        try{
            Map<Object, Object> result = redis.opsForHash().entries(userKey);
            return result.entrySet().stream().collect(Collectors.toMap(
                    e -> e.getKey().toString(),
                    e -> e.getValue().toString()));
        } catch(Exception e) {
            log.error("Redis get All Tracks 실패 : {}", e.getMessage(), e);
            throw new RedisConnectionException("Redis에서 Track의 모든 정보를 조회할 수 없습니다.");
        }
    }

    @Override
    public void removeAllTracks(String userId) {
        String userKey = TRACK_KEY_PREFIX + userId;

        try{
            Map<Object, Object> result = redis.opsForHash().entries(userKey);
            for(Object trackSid : result.keySet()){
                redis.delete(TRACK_KEY_PREFIX + trackSid);
            }
            redis.delete(userKey);
        } catch (Exception e) {
            log.error("Redis Remove All Tracks 실패 : {}", e.getMessage(), e);
            throw new RedisConnectionException("Redis에서 모든 Track 정보를 삭제할 수 없습니다.");
        }
    }


}
