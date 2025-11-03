package com.myjournal.journalApp.service;

import com.myjournal.journalApp.configuration.RateLimiterConfig;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class RateLimiterService {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiterService.class);

    private final RedisTemplate<String, Long> scriptRedisTemplate;
    private final RedisScript<Long> rateLimiterScript;
    private final RateLimiterConfig rateLimiterConfig;

    public RateLimiterService(@Qualifier("scriptRedisTemplate") RedisTemplate<String, Long> scriptRedisTemplate,
                              @Qualifier("rateLimiterScript") RedisScript<Long> rateLimiterScript,
                              RateLimiterConfig rateLimiterConfig) {
        this.scriptRedisTemplate = scriptRedisTemplate;
        this.rateLimiterScript = rateLimiterScript;
        this.rateLimiterConfig = rateLimiterConfig;
    }

    public boolean isRequestAllowed(ObjectId userId) {
        String key = rateLimiterConfig.getKeyPrefix() + userId.toString();

        try {
            Long currentRequests = scriptRedisTemplate.execute(
                    rateLimiterScript,
                    Collections.singletonList(key),
                    String.valueOf(rateLimiterConfig.getWindowSeconds())
            );
            logger.info("Rate limiter script executed. Current requests: {}", currentRequests);
            return currentRequests != null && currentRequests <= rateLimiterConfig.getMaxRequests();
        } catch (Exception e) {
            logger.error("Error executing rate limiter script", e);
            // Redis failure (fail open - allows request)
            return true;
        }
    }
}
