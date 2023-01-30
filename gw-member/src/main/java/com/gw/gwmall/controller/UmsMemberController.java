package com.gw.gwmall.controller;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.api.TokenInfo;
import com.gw.gwmall.common.constant.RedisMemberPrefix;
import com.gw.gwmall.model.UmsMember;
import com.gw.gwmall.model.UmsMemberReceiveAddress;
import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import com.gw.gwmall.service.UmsMemberCenterService;
import com.gw.gwmall.service.UmsMemberReceiveAddressService;
import com.gw.gwmall.service.UmsMemberService;
import com.ramostear.captcha.HappyCaptcha;
import com.ramostear.captcha.support.CaptchaStyle;
import com.ramostear.captcha.support.CaptchaType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 会员登录注册管理Controller
 */
@Controller
@Api(tags = "UmsMemberController", description = "会员登录注册管理")
@RequestMapping("/sso")
@Slf4j
public class UmsMemberController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private UmsMemberCenterService umsMemberCenterService;

    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;

    @Autowired
    private RedisOpsExtUtil redisOpsUtil;

    @ApiOperation("会员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String telephone,
                                 @RequestParam String authCode) {
        return memberService.register(username, password, telephone, authCode);
    }

    @GetMapping("/verifyCode")
    public void generateImg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HappyCaptcha.require(req,resp)
                .style(CaptchaStyle.ANIM) //动画 or 图片
                .type(CaptchaType.ARITHMETIC_ZH) // 中文简体加、减、乘、除
                .build().finish();
    }

    @ApiOperation("会员登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String verifyCode,
                              HttpServletRequest request) {
        if(!HappyCaptcha.verification(request, verifyCode, true)){
            return CommonResult.failed("请填入正确的验证码");
        }
        TokenInfo tokenInfo = memberService.login(username, password);
        if (tokenInfo == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenInfo.getAccess_token());
        tokenMap.put("tokenHead", tokenHead);
        tokenMap.put("refreshToken",tokenInfo.getRefresh_token());
        String memberIdStr = tokenInfo.getAdditionalInfo().get("memberId");
        tokenMap.put("memberId",memberIdStr);
        tokenMap.put("nickName",username);

        /*用户登录成功后，将用户相关信息存入Redis，12小时后过期*/
        Long memberId = Long.valueOf(memberIdStr);
        UmsMember memberInfo = umsMemberCenterService.getMemberInfo(Long.valueOf(memberId));
        redisOpsUtil.set(RedisMemberPrefix.MEMBER_INFO_PREFIX+memberIdStr,memberInfo,60*60*12, TimeUnit.SECONDS);
        List<UmsMemberReceiveAddress> addressList = memberReceiveAddressService.list(memberId);
        redisOpsUtil.putListAllRight(RedisMemberPrefix.MEMBER_ADDRESS_PREFIX+memberIdStr,addressList);
        redisOpsUtil.expire(RedisMemberPrefix.MEMBER_ADDRESS_PREFIX+memberIdStr,60*60*12, TimeUnit.SECONDS);

        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取验证码")
    @RequestMapping(value = "/getAuthCode", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getAuthCode(@RequestParam String telephone) {
        return memberService.generateAuthCode(telephone);
    }

    @ApiOperation("修改密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@RequestParam String telephone,
                                 @RequestParam String password,
                                 @RequestParam String authCode) {
        return memberService.updatePassword(telephone,password,authCode);
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "获取当前登陆用户")
    @RequestMapping(value = "/getCurrentMember", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsMember> getCurrentMember(){
        UmsMember umsMember = memberService.getCurrentMember();
        log.info("当前登陆用户:{}",umsMember.toString());
        return CommonResult.success(umsMember);
    }
}
