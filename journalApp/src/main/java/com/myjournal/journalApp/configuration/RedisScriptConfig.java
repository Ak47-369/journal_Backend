package com.myjournal.journalApp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class RedisScriptConfig {
    @Bean
    public RedisScript<Long> rateLimiterScript(){
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setLocation(new ClassPathResource("scripts/rate-limiter.lua"));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
