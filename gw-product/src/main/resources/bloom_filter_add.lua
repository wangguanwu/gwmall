local bloomName = KEYS[1]
local value = ARGV[1]

-- bloomFilter

local result = redis.call('BF.ADD', bloomName, value)
return result