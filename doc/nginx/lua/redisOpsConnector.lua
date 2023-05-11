local rc = require("resty.connector").new({
    connect_timeout = 150,
    send_timeout = 1000,
    read_timeout = 1000,
    keepalive_timeout = 10000,
    keepalive_poolsize = 100,
    host = "127.0.0.1",
    port = 6379,

})

-- 关闭redis连接的工具方法，其实是放入连接池
local function close_redis(redis)
    local ok, err = rc:set_keepalive(redis)
    if not ok then
        ngx.log(ngx.ERR, "放入连接池失败 : ", err)
    end
end

-- 查询redis的方法 ip和port是redis地址，key是查询的key
local function read_redis(ip, port, key)
    -- 获取一个连接
    local redis, err = rc:connect()
    if  err then
        ngx.log(ngx.ERR, "连接redis失败 : ", err)
        return nil
    end
    -- 查询redis
    local resp, commandErr = redis:get(key)
    -- 查询失败处理
    if not resp then
        ngx.log(ngx.ERR, "查询Redis失败: ", commandErr, ", key = " , key)
    end
    --得到的数据为空处理
    if resp == ngx.null then
        resp = nil
        ngx.log(ngx.ERR, "查询Redis数据为空, key = ", key)
    end
    close_redis(redis)
    return resp
end

-- 封装函数，发送http请求，并解析响应
local function read_http(path, params)
    local resp = ngx.location.capture(path,{
        method = ngx.HTTP_GET,
        args = params,
    })
    if not resp then
        -- 记录错误信息，返回404
        ngx.log(ngx.ERR, "http查询失败, path: ", path , ", args: ", args)
        ngx.exit(404)
    end
    return resp.body
end
-- 将方法导出
local _M = {
    read_http = read_http,
    read_redis = read_redis
}
return _M