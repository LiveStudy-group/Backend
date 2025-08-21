package org.livestudy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redis;
    private final Duration ttl = Duration.ofDays(14);

    public String issue(Long userId) {
        String raw = randomUrlSafe(32);
        String hash = sha256(raw);
        redis.opsForValue().set(key(hash), String.valueOf(userId), ttl);
        return raw;
    }

    public Long verifyAndConsume(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String k = key(sha256(raw));
        String userId = redis.opsForValue().get(k);
        if (userId == null) {
            return null;
        }
        redis.delete(k);
        return Long.valueOf(userId);
    }

    private static String key(String hash) {
        return "refresh:" + hash;
    }
    private static String randomUrlSafe(int bytes) {
        byte[] buf = new byte[bytes];
        new SecureRandom().nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
