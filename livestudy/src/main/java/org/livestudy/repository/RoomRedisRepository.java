package org.livestudy.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class RoomRedisRepository {

    private final StringRedisTemplate redisTemplate;

    // 현재 방 인원 +1
    public Long incrementRoomCount(String roomId) {
        return redisTemplate.opsForValue().increment("room:" + roomId + ":count");
    }

    // 현재 방 인원 -1
    public void decrementRoomCount(String roomId) {
        redisTemplate.opsForValue().decrement("room:" + roomId + ":count");
    }

    // 유저가 입장한 방 정보 저장
    public void setUserRoom(String userId, String roomId) {
        redisTemplate.opsForValue().set("user:" + userId + ":room", roomId);
    }

    // 유저가 입장한 방 조회
    public String getUserRoom(String userId) {
        return redisTemplate.opsForValue().get("user:" + userId + ":room");
    }

    // 유저 방 정보 제거 (퇴장 처리)
    public void deleteUserRoom(String userId) {
        redisTemplate.delete("user:" + userId + ":room");
    }
}

