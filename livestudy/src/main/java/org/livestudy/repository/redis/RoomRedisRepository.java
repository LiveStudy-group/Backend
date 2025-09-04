package org.livestudy.repository.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.logging.Logger;

@Repository
public class RoomRedisRepository {

    private final RedisTemplate<String, String> roomRedisTemplate;

    private final Logger log = Logger.getLogger(RoomRedisRepository.class.getName());

    public RoomRedisRepository(@Qualifier("roomStringRedisTemplate") RedisTemplate<String, String> roomRedisTemplate) {
        this.roomRedisTemplate = roomRedisTemplate;
    }

    // 현재 방 인원 +1
    public void incrementRoomCount(String roomId) {
        log.info("👉 Redis count increment 시도중: {}");
        roomRedisTemplate.opsForValue().increment("room:" + roomId + ":count");
    }

    // 현재 방 인원 -1
    public void decrementRoomCount(String roomId) {
        roomRedisTemplate.opsForValue().decrement("room:" + roomId + ":count");
    }

    // 유저가 입장한 방 정보 저장
    public void setUserRoom(String userId, String roomId) {
        roomRedisTemplate.opsForValue().set("user:" + userId, roomId);
    }

    // 유저가 입장한 방 조회
    public String getUserRoom(String userId) {
        return roomRedisTemplate.opsForValue().get("user:" + userId);  // null 반환 허용

    }

    // 유저 방 정보 제거 (퇴장 처리)
    public void deleteUserRoom(String userId) {
        roomRedisTemplate.delete("user:" + userId);
    }

    // 특정 방 ID 정보의 인원수 구하기
    public String getRoomCount(String roomId) {
        return roomRedisTemplate.opsForValue().get("room:" + roomId + ":count");
    }

}

