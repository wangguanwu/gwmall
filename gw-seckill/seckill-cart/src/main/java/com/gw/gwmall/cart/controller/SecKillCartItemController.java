package com.gw.gwmall.cart.controller;

import com.gw.gwmall.cart.service.SecKillConfirmOrderService;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.exception.BusinessException;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author guanwu
 */
@Controller
@RequestMapping("/skcart")
@Api(tags = "SecKillCartItemController")
public class SecKillCartItemController {

    @Resource
    private SecKillConfirmOrderService secKillConfirmOrderService;

    /**
     * 秒杀订单确认页
     * @param productId
     * @param memberId
     * @return
     * @throws BusinessException
     */
    @RequestMapping(value = "/generateConfirmOrder",method = RequestMethod.POST)
    @ResponseBody
    public CommonResult generateMiaoShaConfirmOrder(@RequestParam("productId") Long productId,
                                                    @RequestHeader("memberId") Long memberId,
                                                    @RequestParam("flashPromotionId") Long flashPromotionId) throws BusinessException {
        return secKillConfirmOrderService.generateConfirmSecKillOrder(productId, memberId,"", flashPromotionId);
    }

    /* PO:
          1、从session共享角度来说，验证码应该放入Redis才是正确的，
          直接参考商城系统中接入spring session解决验证码共享存储的问题
          2、或者生成完然后从session取出来自行放到redis即可
          怎么获得HappyCaptcha生成的验证码呢？HappyCaptcha是放到session的，
          怎么从session中获得呢，看看HappyCaptcha.verification()就知道答案：
          " String captcha = (String)request.getSession().getAttribute(SESSION_KEY); "
          ，获得后然后再删除掉session中的验证码。
          3、验证码放入主Redis后，如果选择从Nginx直接读取从Redis的方式，需要注意
          Redis主从同步的延迟问题，解决方案可以在Lua脚本中引入以下两者之一：
           a.休眠后重试”os.execute("sleep " .. n)”
           b.读从Redis未果，则读主Redis
          4、验证码本身也可以独立为一个微服务
          5、当生成验证码本身成为性能瓶颈，可以 验证码微服务集群化 或者 预生成批量验证码并缓存，
          但是缓存的内容除了验证码的文字结果外，验证图片也要缓存
      */
    @ApiOperation("生成验证码-限流")
    @GetMapping("/verifyCode")
    public void generateImg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HappyCaptcha.require(req,resp)
                .style(CaptchaStyle.ANIM) //动画 or 图片
                .type(CaptchaType.ARITHMETIC_ZH) // 中文简体加、减、乘、除
                .build().finish();
    }

    @ApiOperation("检查验证码")
    @RequestMapping(value = "/checkCode", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult checkCode(@RequestParam String verifyCode,
                                  HttpServletRequest request) throws IOException {
        if(!HappyCaptcha.verification(request, verifyCode, true)){
            return CommonResult.failed("请填入正确的验证码");
        }else{
            return CommonResult.success("验证通过");
        }
    }

}
