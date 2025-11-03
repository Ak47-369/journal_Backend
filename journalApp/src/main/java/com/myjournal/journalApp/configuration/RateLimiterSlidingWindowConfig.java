package com.myjournal.journalApp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limiter")
@Data
public class RateLimiterSlidingWindowConfig {
    private String keyPrefix;
    private int windowSeconds;
    private int maxRequests;
}
