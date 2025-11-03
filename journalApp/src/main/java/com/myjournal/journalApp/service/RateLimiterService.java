package com.myjournal.journalApp.service;

import com.myjournal.journalApp.configuration.RateLimiterTokenBucketConfig;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Collections;


@Service
@Slf4j
public class RateLimiterService {
    private final RedisTemplate<String, Long> scriptRedisTemplate;
    private final RedisScript<Long> rateLimiterScript;
    private final RateLimiterTokenBucketConfig rateLimiterConfig;

    public RateLimiterService(@Qualifier("scriptRedisTemplate") RedisTemplate<String, Long> scriptRedisTemplate,
                              @Qualifier("rateLimiterScript") RedisScript<Long> rateLimiterScript,
                              RateLimiterTokenBucketConfig rateLimiterConfig) {
        this.scriptRedisTemplate = scriptRedisTemplate;
        this.rateLimiterScript = rateLimiterScript;
        this.rateLimiterConfig = rateLimiterConfig;
    }

    public boolean isRequestAllowed(ObjectId userId) {
        String key = rateLimiterConfig.getKeyPrefix() + userId.toString();
        long nowInSeconds = Instant.now().getEpochSecond();
        try {
            Long isAllowed = scriptRedisTemplate.execute(
                    rateLimiterScript,
                    Collections.singletonList(key),
                    String.valueOf(rateLimiterConfig.getCapacity()),
                    String.valueOf(rateLimiterConfig.getRefillRate()),
                    String.valueOf(nowInSeconds),
                    String.valueOf(rateLimiterConfig.getTokensPerRequest())
            );
            log.info("Rate limiter script executed. Current requests {}", isAllowed == 1 ? "is Allowed" : "not Allowed");
            return isAllowed == 1;
        } catch (Exception e) {
            log.error("Error executing rate limiter script", e);
            // Redis failure (As of now we will fail open - allows request)
            return true;
        }
    }
}
