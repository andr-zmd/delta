package com.github.andrz25.delta.user_service.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.github.andrz25.delta.user_service.response.UserResponse;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    RedisCacheConfiguration cacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        SerializationPair.fromSerializer(new JacksonJsonRedisSerializer<>(UserResponse.class)));
    }
}
