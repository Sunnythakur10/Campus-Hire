package com.campushire.campus_hire.shared.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisTokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public RedisTokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // --- THE METHODS YOU WERE MISSING ---

    // 1. This puts the token into the Redis "jail"
    public void blacklistToken(String jti, long remainingTimeSeconds) {
        String key = "blacklist:token:" + jti;
        redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofSeconds(remainingTimeSeconds));
    }

    // 2. The Bouncer (JwtAuthenticationFilter) uses this to check if a token is in jail
    public boolean isBlacklisted(String jti) {
        String key = "blacklist:token:" + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}