-- 导入redisOps函数库
local redisOps = require('redisOps')
local read_redis = redisOps.read_redis
-- 导入cjson库
local cjson = require('cjson')
-- 导入共享词典，本地缓存
-- 本地缓存的主要目的为库存检查，当商品的库存<=0时，提前终止秒杀
-- 这里从业务上来说，同样需要解决退单等引发的库存增加允许重新秒杀的情况，
-- 解决思路：同样可以订阅对应的Redis的channel，本次不做具体实现，Lua订阅Redis的Channel的参考代码写在RedisExtOps.lua中
local item_cache = ngx.shared.stock_cache

-- 封装查询函数
function read_data(key, expire)
    -- 查询本地缓存
    local val = item_cache:get(key)
    if not val then
        ngx.log(ngx.ERR, "x，尝试查询Redis， key: ", key)
        -- 查询redis
        val = read_redis("127.0.0.1", 6379, key)
        -- 判断查询结果
        if not val then
            ngx.log(ngx.ERR, "redis查询失败，key: ", key)
            -- redis查询失败，给一个缺省值
            val = 0
        end
    end
    -- 查询成功，把数据写入本地缓存,expire秒后过期
    if tonumber(val) <= 0 then
        item_cache:set(key, val, expire)
    end
    -- 返回数据
    ngx.log(ngx.ERR, "redis查询成功, key: ", key, ",value: ", val)
    return val
end

-- 获取请求参数中的productId，也可以使用ngx.req.get_uri_args["productId"]，req.get_uri_args在productId有多个时，会返回一个table
local product_id = ngx.var.arg_productId

-- 查询库存信息
local stock = read_data("miaosha:stock:cache:"..product_id, 3600)

-- 返回结果
ngx.say(cjson.encode(stock))