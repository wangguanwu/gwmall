local bloomName = KEYS[1]
local probability = tonumber(ARGV[1])
local scale = tonumber(ARGV[2])
-- test if bloomFilter exist
local exist = redis.call('exists', bloomName);
if (exist == 1) then
    return exist;
end
-- BF.RESERVE customFilter 0.0001 600000
local result = redis.call('BF.RESERVE', bloomName, probability, scale)
return result