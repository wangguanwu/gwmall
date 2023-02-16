package com.gw.gwmall.ordercurrent.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.common.collect.Maps;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.exception.BusinessException;
import com.gw.gwmall.ordercurrent.component.rocketmq.pay.OrderMessageSender;
import com.gw.gwmall.ordercurrent.component.trade.alipay.config.Configs;
import com.gw.gwmall.ordercurrent.domain.MqCancelOrder;
import com.gw.gwmall.ordercurrent.domain.OmsOrderDetail;
import com.gw.gwmall.ordercurrent.domain.OrderParam;
import com.gw.gwmall.ordercurrent.service.OmsPortalOrderService;
import com.gw.gwmall.ordercurrent.service.TradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 订单管理Controller
 */
@Slf4j
@Controller
@Api(tags = "OmsPortalOrderController",description = "订单管理")
@RequestMapping("/order")
public class OmsPortalOrderController {

    @Autowired
    private OmsPortalOrderService portalOrderService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OrderMessageSender orderMessageSender;

    @ApiOperation("根据购物车信息生成订单")
    @RequestMapping(value = "/generateOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateOrder(@RequestBody OrderParam orderParam,
                                      @RequestHeader("memberId") Long memberId) throws BusinessException {
        return portalOrderService.generateOrder(orderParam,memberId);
    }

//    @ApiOperation("根据购物车信息生成订单")
//    @RequestMapping(value = "/generateOrder",method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult generateOrder(@RequestBody OrderParam orderParam,
//                                      @RequestHeader("memberId") Long memberId) throws BusinessException {
//        Long orderId = portalOrderService.generateOrderId(memberId);
//        orderParam.setOrderId(orderId);
//        orderMessageSender.sendCreateOrderMsg(orderParam,memberId);
////        return portalOrderService.generateOrder(orderParam,memberId);
//        Map<String,Object> res = new HashMap<>();
//        res.put("orderId",orderId);
//        return CommonResult.success(res);
//    }

    @ApiOperation("获取orderId，可避免重复下单")
    @GetMapping(value = "/generateOrderId")
    @ResponseBody
    public CommonResult generateOrderId(@RequestHeader("memberId") Long memberId) throws BusinessException {
        Long orderId = portalOrderService.generateOrderId(memberId);
        return CommonResult.success(orderId);
    }

    @ApiOperation("获取指定订单详情")
    @RequestMapping(value = {"/specificOrderDetail","/orderDetail"},method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public CommonResult specificOrderDetail(@RequestParam Long orderId){
        return portalOrderService.getDetailOrder(orderId);
    }

    @ApiOperation("获取指定业务编号订单详情")
    @RequestMapping(value = "/specificOrderSnDetail",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public CommonResult specificOrderSnDetail(@RequestParam String orderSn){
        /*在我们的实现中，业务订单orderSn和订单内部编号orderId是同一个，
        所以这里可以简单处理，实际工作中如果两者不一样，
        保证根据一定的规则可以从orderSn获得orderId即可*/
        return portalOrderService.getDetailOrder(Long.valueOf(orderSn));
    }

//    @ApiOperation("支付成功修改订单状态")
//    @ApiImplicitParams({@ApiImplicitParam(name = "payType", value = "支付方式:0->未支付,1->支付宝支付,2->微信支付",
//            allowableValues = "1,2", paramType = "query", dataType = "integer")})
//    @RequestMapping(value = "/paySuccess/{orderId}",method = RequestMethod.POST)
//    @ResponseBody
//    public void paySuccess(@PathVariable Long orderId,Integer payType) {
//        if(payType > 2 || payType < 0){
//            throw new IllegalArgumentException("支付类型不正确，平台目前仅支持支付宝与微信支付");
//        }
//        portalOrderService.paySuccess(orderId,payType);
//    }

    @ApiOperation("批量检查超时订单并取消")
    @RequestMapping(value = "/cancelTimeOutOrders",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelTimeOutOrders(){
        return portalOrderService.cancelTimeOutOrder();
    }

    @ApiOperation("取消单个超时订单")
    @RequestMapping(value = "/cancelOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancelOrder(Long orderId,@RequestHeader("memberId") Long memberId){
        //todo
        MqCancelOrder mqCancelOrder = new MqCancelOrder();
        mqCancelOrder.setMemberId(memberId);
        mqCancelOrder.setOrderId(orderId);

        portalOrderService.sendDelayMessageCancelOrder(mqCancelOrder);
        return CommonResult.success(null);
    }

    /**
     * 删除订单[逻辑删除],只能status为：3->已完成；4->已关闭；5->无效订单，才可以删除
     * ，否则只能先取消订单然后删除。
     * @param orderId
     * @return
     */
    @ApiOperation(value = "逻辑删除指定订单",notes = "status为：3->已完成；4->已关闭；5->无效订单，才可以删除，否则只能先取消订单然后删除")
    @RequestMapping(value = "/deleteOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteOrder(Long orderId){
        int total = portalOrderService.deleteOrder(orderId);
        if(total > 0){
            return CommonResult.success("有："+total+"：条订单被删除");
        }else{
            return CommonResult.failed("订单已经被删除或者没有符合条件的订单");
        }
    }
    /**
     * 订单服务由会员服务调用，会员服务传来会员：ID
     * @param memberId
     * @param status
     *      null查询所有
     *      订单状态0->待付款；1->待发货；2->已发货；3->已完成;4->已关闭；
     * @return
     */
    @ApiOperation("用户订单查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "用户ID", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "status", value = "订单状态:0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭",
                    allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")})
    @RequestMapping(value = "/list/userOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<OmsOrderDetail>> findMemberOrderList(
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "memberId") Long memberId,
            @RequestParam(value = "status",required = false) Integer status){

        if(memberId == null || (status!=null && status > 4)){
            return CommonResult.validateFailed();
        }
        return portalOrderService.findMemberOrderList(pageSize,pageNum,memberId,status);
    }

    /**
     * 订单支付逻辑：支付支持两种方式：alipay,wechat
     * @param orderId
     * @param payType
     * @return
     */
    @ApiOperation("订单支付#实现支付宝支付{微信支付暂未实现}")
    @ApiImplicitParams({@ApiImplicitParam(name = "payType", value = "支付方式:0->未支付,1->支付宝支付,2->微信支付",
                    allowableValues = "1,2", paramType = "query", dataType = "integer")})
    @RequestMapping(value = "/tradeQrCode",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult tradeQrCode(@RequestParam(value = "orderId") Long orderId,
                                    @RequestHeader("memberId") Long memberId,
                                    @RequestParam(value = "payType") Integer payType){
        if(payType > 2 || payType < 0){
            throw new IllegalArgumentException("支付类型不正确，平台目前仅支持支付宝与微信支付");
        }
        orderMessageSender.sendCreateOrderMsg(orderId,memberId);
        return tradeService.tradeQrCode(orderId,payType,memberId);
    }


    @ApiOperation("订单支付状态查询,手动查询#实现支付宝查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "payType", value = "支付方式:0->未支付,1->支付宝支付,2->微信支付",
            allowableValues = "1,2", paramType = "query", dataType = "integer")})
    @RequestMapping(value = "/tradeStatusQuery",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult tradeStatusQuery(@RequestParam(value = "orderId") Long orderId,
                                         @RequestParam(value = "payType") Integer payType){

        if(payType > 2 || payType < 0){
            throw new IllegalArgumentException("支付类型不正确，平台目前仅支持支付宝与微信支付");
        }
        return tradeService.tradeStatusQuery(orderId,payType);
    }

    @ApiOperation("支付成功的回调")
    @ApiImplicitParams({@ApiImplicitParam(name = "payType", value = "支付方式:0->未支付,1->支付宝支付,2->微信支付",
            allowableValues = "1,2", paramType = "query", dataType = "integer")})
    @RequestMapping(value = "/paySuccess/{payType}",method = RequestMethod.POST)
    @ResponseBody
    public void paySuccess(@PathVariable Integer payType,
                           HttpServletRequest request,
                           HttpServletResponse response) throws AlipayApiException {
        if(payType > 2 || payType < 0){
            throw new IllegalArgumentException("支付类型不正确，平台目前仅支持支付宝与微信支付");
        }
        if(payType == 1){//支付宝支付
            //1、获取request里所有与alipay相关的参数，封装成一个map
            Map<String,String> param = Maps.newHashMap();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()){
                String parameterName = parameterNames.nextElement();
                log.info("alipay callback parameters:-->"
                        +parameterName+":->" +request.getParameter(parameterName));
                if(!parameterName.toLowerCase().equals("sign_type")){
                    param.put(parameterName,request.getParameter(parameterName));
                }
            }
            // 2、验证请求是否是alipay返回的请求内容【验证请求合法性】
            // 很重要
            boolean isPassed = AlipaySignature.rsaCheckV2(param, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            PrintWriter out = null;
            try {
                out = response.getWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isPassed){
                Long orderId = Long.parseLong(param.get("out_trade_no"));
                int count = portalOrderService.paySuccess(orderId,payType);
                if(count > 0){
                    log.info("支付成功，订单完成支付");
                    out.print("success");
                }else{
                    log.info("支付失败，订单未能完成支付");
                    out.print("unSuccess");
                }
            }else{
                log.info("支付失败，订单未能完成支付");
                out.print("unSuccess");
            }
        }else if(payType == 2){//微信支付

        }
    }
}
