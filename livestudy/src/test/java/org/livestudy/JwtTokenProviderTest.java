package org.livestudy;

import org.junit.jupiter.api.Test;
import org.livestudy.config.RedisConfig;
import org.livestudy.domain.user.User;
import org.livestudy.security.SecurityUser;
import org.livestudy.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(RedisConfig.class)
public class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;



    @Test
    void generate_and_validate_token(){
        // 예시

        String email = "test@example.com";
        Long userId = 1L;

        User user = User.builder()
                .id(userId)
                .email(email)
                .build();

        SecurityUser securityUser = new SecurityUser(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                securityUser, null, securityUser.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(auth);

        assertTrue(jwtTokenProvider.validateToken(token));

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityUser principal =  (SecurityUser) authentication.getPrincipal();

        assertEquals(email, principal.getUsername());
        assertEquals(userId, principal.getUser().getId());
    }
}
