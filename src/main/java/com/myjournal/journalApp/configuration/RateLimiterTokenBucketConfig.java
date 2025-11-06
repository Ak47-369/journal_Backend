package com.myjournal.journalApp.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limiter.token-bucket")
@Data
public class RateLimiterTokenBucketConfig {
    private String keyPrefix;
    private double capacity;
    private double refillRate;
    private double tokensPerRequest;
}
