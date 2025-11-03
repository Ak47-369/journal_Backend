-- KEYS[1] = The unique key for the user (e.g rate-limit:payment:user123)
-- ARGV[1] = Bucket Capacity (Max tokens e.g 100)
-- ARGV[2] = Refill Rate per Second
-- ARGV[3] = Current TimeStamp (in whole seconds)
-- ARGV[4] = Tokens to Take (e.g 1)

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local current_time = tonumber(ARGV[3])
local tokens_to_take = tonumber(ARGV[4])

-- Get the current bucket state (tokens and last refill time)
local data = redis.call("HMGET",key, "tokens", "last_refill_time")
local last_tokens = tonumber(data[1])
local last_refill_time = tonumber(data[2])

-- If this is the first request , initialize the bucket
if last_tokens == nil then
    last_tokens = capacity
    last_refill_time = current_time
end

-- Calculate time passed and tokens to add
local elapsed_time = math.max(0, current_time - last_refill_time)
local new_tokens = elapsed_time * refill_rate;

-- Calculate the new token count, capped at max capacity
local current_tokens = math.min(capacity, last_tokens + new_tokens)

-- Check if user have enough tokens
local allowed = 0
if current_tokens >= tokens_to_take then
    redis.call("HMSET", key, "tokens", current_tokens - tokens_to_take,"last_refill_time", current_time)
    allowed = 1
else
    -- Just update the state
    redis.call("HMSET", key, "tokens", current_tokens, "last_refill_time", current_time)
end

return allowed

