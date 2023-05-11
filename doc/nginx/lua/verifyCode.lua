-- 导入redisOps函数库
local redisCluster = require('resty.redisClusterOps')
local cluster_hget = redisCluster.cluster_hget
-- 导入cjson库
local cjson = require('cjson')

-- 封装查询函数
local function read_data(key, field)
    -- 查询redis
    val = cluster_hget(key, field)
    -- 判断查询结果
    if not val then
        ngx.log(ngx.ERR, "redis查询失败，key: ", key)
        -- redis查询失败，给一个缺省值
        val = nil
    end
    return val
end

local sessionKeyEnc = ngx.var.cookie_session

-- 查询库存信息
ngx.log(ngx.ERR, "session key: ", sessionKeyEnc)
local sessionKey = ngx.decode_base64(sessionKeyEnc)
ngx.log(ngx.ERR, "session key: ", sessionKey)
local val = read_data("spring:session:sessions:" .. sessionKey, "sessionAttr:happy-captcha")
local verifyCodeStr = nil
for k, v in ipairs(val) do
    verifyCodeStr = v
    ngx.log(ngx.ERR, " key :", k, ", value:", cjson.encode(v))
    break
end
ngx.req.read_body()
local verifyCode = ngx.var.arg_verifyCode;
ngx.log(ngx.ERR, "verifyCode ", verifyCode)
--local verifyCode = reqUriParams['verifyCode']
--local params = ""
--for k, v in ipairs(reqUriParams) do
--    params = k .. ':' .. v .. ','
--end
local redisCode = string.match(verifyCodeStr, '[-]*%d+')
ngx.log(ngx.ERR, "redis get verifyCode: ",  redisCode)
local res = {}
if (verifyCode == redisCode) then
    ngx.status = 200
    res.msg = "success"
    res.code = 200
    res.data = nil
else
    ngx.status = 403
    res.data = nil
    res.msg = 'auth failed'
end

-- 返回结果
ngx.say(cjson.encode(res))