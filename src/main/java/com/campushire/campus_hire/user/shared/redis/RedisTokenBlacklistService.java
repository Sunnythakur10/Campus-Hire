package com.campushire.campus_hire.user.shared.redis;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.stereotype.Service;

@Service
public class RedisTokenBlacklistService {
    private final SpringRedisTemplate redisTemplate;
    public RedisTokenBlacklistService(SpringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }
}
