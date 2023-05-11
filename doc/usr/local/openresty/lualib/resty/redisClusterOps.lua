-- 导入redis的Lua模块
local redisCluster = require 'resty.rediscluster'
local cjson = require "cjson"
ngx.log(ngx.ERR, "加载rediscluster模块成功", type(redisCluster))
local config = {
    name = "rediscluster",
    serv_list = {
        {ip="127.0.0.1", port = 8001},
        {ip="127.0.0.1", port = 8002},
        {ip="127.0.0.1", port = 8003},
        {ip="127.0.0.1", port = 8004},
        {ip="127.0.0.1", port = 8005},
        {ip="127.0.0.1", port = 8006}
    }
    --keepalive_timeout = 60000,
    --keepalive_cons = 1000,
    --connection_timeout = 10,
    --max_redirection = 5,
    --max_connection_attempts = 1
}

-- 查询redis的方法 ip和port是redis地址，key是查询的key
local function open_conn(config)
    local red = redisCluster:new(config)
    return red
end

local function close_conn(conn)
    conn:close()
end

local function cluster_get(key)
    local red = open_conn(config)
    red:init_pipeline()
    red:get(key)
    local results = red:commit_pipeline()
    if(results == ngx.null) then
        results = nil
    end
    ngx.log(ngx.ERR, "start to redis_get(), value=", cjson.encode(results))
    close_conn(red)
    return results
end

local function cluster_hget(key, field)
    local red = open_conn(config)
    red:init_pipeline()
    red:hget(key, field)
    local results = red:commit_pipeline()
    if(results == ngx.null) then
        results = nil
    end
    ngx.log(ngx.ERR, "start to redis_get(), value=", cjson.encode(results))
    close_conn(red)
    return results
end

-- 将方法导出
ngx.log(ngx.ERR, "导出结果")
local _Cluster = {
    hello="hello",
    world="world",
    cluster_get = cluster_get,
    cluster_hget = cluster_hget
}
return _Cluster