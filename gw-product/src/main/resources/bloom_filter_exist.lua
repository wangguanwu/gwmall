local bloomName = KEYS[1]
local value = ARGV[1]

-- bloomFilter
return redis.call('BF.EXISTS', bloomName, value)
