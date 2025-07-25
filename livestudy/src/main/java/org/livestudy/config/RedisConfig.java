package org.livestudy.config;

import org.livestudy.dto.TrackInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean(name = "TrackRedisTemplate")
    @Primary
    public RedisTemplate<String, TrackInfo> trackRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, TrackInfo> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Key는 String으로 설정
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // Value는 Jackson2JsonRedisSerializer<TrackInfo>로 설정한다.
        Jackson2JsonRedisSerializer<TrackInfo> jackson2Serializer =
                new Jackson2JsonRedisSerializer<>(TrackInfo.class);

        redisTemplate.setValueSerializer(jackson2Serializer);
        redisTemplate.setHashValueSerializer(jackson2Serializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean(name = "roomStringRedisTemplate")
    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
