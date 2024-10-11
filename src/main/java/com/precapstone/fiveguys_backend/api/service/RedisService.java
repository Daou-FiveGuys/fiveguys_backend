package com.precapstone.fiveguys_backend.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
