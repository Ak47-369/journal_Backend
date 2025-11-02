package com.myjournal.journalApp.configuration;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching // This enables Spring's annotation-driven cache management
public class CacheConfig {

    // Serializer for String key
    private final StringRedisSerializer keySerializer = new StringRedisSerializer();
    // Serializer for JSON Values
    private final GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();


    /**
     * This @Bean creates the central CacheManager that Spring's @Cacheable
     * annotation will use.
     *
     * @param connectionFactory The auto-configured Redis connection.
     * @return A fully configured RedisCacheManager.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // Default Cache Configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues() // Don't cache 'null' values (a best practice)
                .serializeKeysWith( // Set key serializer to String
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith( // Set value serializer to JSON
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        // Custom Cache Configuration
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("userCache", // This name MUST match @Cacheable("userCache")
                defaultConfig.entryTtl(Duration.ofMinutes(30))
        );

        cacheConfigurations.put("journalEntryCache",
                defaultConfig.entryTtl(Duration.ofMinutes(5))
        );

        cacheConfigurations.put("staticDataCache",
                defaultConfig.entryTtl(Duration.ofHours(24))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig) // Set the default config
                .withInitialCacheConfigurations(cacheConfigurations) // Set all the per-cache configs
                .build();
    }

    /**
     * This @Bean configures the "Manual" RedisTemplate for direct injections.
     * It ensures that any manual operations use the same JSON serialization
     * as the "Automagic" @Cacheable cache.
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
