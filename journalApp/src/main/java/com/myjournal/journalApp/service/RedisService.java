package com.myjournal.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Retrieves a value from Redis and casts it to the desired type.
     * The RedisTemplate is configured to handle JSON deserialization automatically.
     */
    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object cachedData = redisTemplate.opsForValue().get(key);
            if (cachedData != null) {
                log.info("Cache Hit! for key: {}", key);
                return entityClass.cast(cachedData);
            }
            log.info("Cache Miss! for key: {}", key);
        } catch (Exception e) {
            log.error("Error retrieving cached data for key: {}", key, e);
        }
        return null;
    }

    /**
     * Sets a value in Redis with a specific Time-To-Live (TTL).
     * This uses an overload of the .set() method that is now available because
     * the RedisTemplate is correctly configured.
     *
     * @param key     The key to store the data under.
     * @param value   The value to be stored (will be auto-serialized to JSON).
     * @param timeout The duration for the TTL.
     * @param unit    The time unit for the timeout (e.g., TimeUnit.SECONDS).
     */
    public <T> void set(String key, T value, long timeout, TimeUnit unit) {
        try {
            // No more manual JSON conversion!
            // This command is atomic and sets the value and TTL in one go.
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.info("Data cached for key: {} with TTL: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error caching data for key: {}", key, e);
        }
    }

    /**
     * Sets a value in Redis with no expiration.
     */
    public <T> void set(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.info("Data cached for key: {} with no TTL", key);
        } catch (Exception e) {
            log.error("Error caching data for key: {}", key, e);
        }
    }

    /**
     * Deletes a key from Redis.
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.info("Deleted cache for key: {}", key);
        } catch (Exception e) {
            log.error("Error deleting cache for key: {}", key, e);
        }
    }
}
