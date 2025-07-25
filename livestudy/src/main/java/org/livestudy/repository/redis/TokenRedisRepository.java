package org.livestudy.repository.redis;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;


@Repository
public class TokenRedisRepository {


    private final RedisTemplate<String, String> redisTemplate;

    public TokenRedisRepository(@Qualifier("roomStringRedisTemplate")RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 블랙리스트 등록
    public void blacklistToken(String token, long expirationSeconds){
        redisTemplate.opsForValue().set("blacklist:" + token, "true", Duration.ofSeconds(expirationSeconds));
    }

    // 블랙리스트 확인
    public boolean isBlacklisted(String token){
        String value = Objects.requireNonNull(redisTemplate.opsForValue().get("blacklist:" + token));
        return "true".equals(value);
    }
}
