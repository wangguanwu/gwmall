package com.gw.gwmall.service;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.api.TokenInfo;
import com.gw.gwmall.model.UmsMember;

import org.springframework.transaction.annotation.Transactional;

/**
 * 会员管理Service
 */
public interface UmsMemberService {
    /**
     * 根据用户名获取会员
     */
    UmsMember getByUsername(String username);

    /**
     * 根据会员编号获取会员
     */
    UmsMember getById(Long id);

    /**
     * 用户注册
     */
    @Transactional
    CommonResult register(String username, String password, String telephone, String authCode);

    /**
     * 生成验证码
     */
    CommonResult generateAuthCode(String telephone);

    /**
     * 修改密码
     */
    @Transactional
    CommonResult updatePassword(String telephone, String password, String authCode);

    /**
     * 获取当前登录会员
     */
    UmsMember getCurrentMember();

    /**
     * 根据会员id修改会员积分
     */
    void updateIntegration(Long id, Integer integration);



    /**
     * 登录后获取token
     */
    TokenInfo login(String username, String password);

    /**
     * 刷新token
     */
    String refreshToken(String token);

    @Transactional
    int updateUmsMember(UmsMember umsMember);
}
