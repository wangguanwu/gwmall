
local config = {
    name = test,
    serv_list = {
        {ip=127.0.0.1, port = 8001},
        {ip=127.0.0.1, port = 8002},
        {ip=127.0.0.1, port = 8003},
        {ip=127.0.0.1, port = 8004},
        {ip=127.0.0.1, port = 8005},
        {ip=127.0.0.1, port = 8006},
    },
}
local redis_cluster = require 'resty.rediscluster'
local red = redis_cluster:new(config)
for i = 1, 2 do
    red:init_pipeline()
    red:set(k1, 'hello')
    red:get(k1)
    red:set(k2, 'world')
    red:get(k2)
    local results = red:commit_pipeline()
    local cjson = require cjson
    ngx.say(cjson.encode(results))
end
red:close()



