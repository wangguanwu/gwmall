local bloomName = KEYS[1]
local probability = ARGV[1]
local capacity = ARGV[2]
local exist = redis.call('exists', bloomName);
if (exist == 1) then
    return "OK"
end
-- BF.RESERVE customFilter 0.0001 1000000
return redis.call('BF.RESERVE', bloomName, probability, capacity)
