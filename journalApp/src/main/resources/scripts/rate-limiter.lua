-- This script is executed ATOMICALLY on Redis Server
-- KEYS[1] = The specific key to increment
-- ARGV[1] = The window expiry time in seconds

-- Increment the key
local current_requests = redis.call("INCR", KEYS[1])

-- Check if its first request, then set expiry time
if current_requests == 1
    then redis.call("EXPIRE", KEYS[1], ARGV[1])
end

return current_requests