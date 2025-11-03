-- This script is executed ATOMICALLY on Redis Server
-- KEYS[1] = The specific key to increment
-- ARGV[1] = The maximum number of requests
-- ARGV[2] = The duration of time window in seconds

local key = KEYS[1]
local max_requests = tonumber(ARGV[1])
local window_seconds = tonumber(ARGV[2])

-- Get current time from Redis for consistency
local now = redis.call("TIME")
local now_micros = (now[1] * 1000000) + now[2]

-- Define window start time
local window_start = now_micros - (window_seconds * 1000000)

-- Remove all old timestamps that are outside the current window
redis.call("ZREMRANGEBYSCORE", key, 0, window_start)

-- Get current no. of requests in window (count remaining timestamps)
local current_requests = redis.call("ZCARD", key)

-- Check if the user is still within their quota
if current_requests < max_requests then
    -- Add current timestamp to sorted set
    redis.call("ZADD", key, now_micros, now_micros)
    -- Set an expiration on the key itself to auto-clean if the user becomes inactive
    redis.call("EXPIRE", key, window_seconds)
    return current_requests + 1
end
-- User has exceeded their quota
return current_requests