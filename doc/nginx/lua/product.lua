-- 导入lua-resty-template函数库
local template = require('resty.template')
local flashPromotionId = ngx.var.arg_flashPromotionId
ngx.log(ngx.ERR, "秒杀活动ID: ", flashPromotionId)
local promotionProductId = ngx.var.arg_promotionProductId
ngx.log(ngx.ERR, "秒杀产品ID: ", promotionProductId)
local templateName = "seckill_"..flashPromotionId.."_"..promotionProductId..".html"
local context = {
    memberId = ngx.var.arg_memberId,
    productId = promotionProductId,
    flashPromotionId = flashPromotionId
}
ngx.log(ngx.ERR, "渲染页面输出，获得当前用户ID: ", context.memberId)
template.render(templateName, context)