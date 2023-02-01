package com.gw.gwmall.mall.service;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.mall.domain.MemberDetails;
import com.gw.gwmall.mall.feign.UmsMemberFeignService;
import com.gw.gwmall.mall.mapper.UmsMemberMapper;
import com.gw.gwmall.mall.model.UmsMember;
import com.gw.gwmall.mall.model.UmsMemberExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
@Component
public class GwUserDetailService implements UserDetailsService {

    /**
     * 方法实现说明:用户登陆
     */
    @Autowired
    private UmsMemberFeignService umsMemberFeignService;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        if(StringUtils.isEmpty(userName)) {
            log.warn("用户登陆用户名为空:{}",userName);
            throw new UsernameNotFoundException("用户名不能为空");
        }

        UmsMember umsMember = getByUsername(userName);

        if(null == umsMember) {
            log.warn("根据用户名没有查询到对应的用户信息:{}",userName);
        }

        log.info("根据用户名:{}获取用户登陆信息:{}",userName,umsMember);

        return new MemberDetails(umsMember);
    }

    /**
     * 方法实现说明:根据用户名获取用户信息
     */
    public UmsMember getByUsername(String username) {
//        UmsMemberExample example = new UmsMemberExample();
//        example.createCriteria().andUsernameEqualTo(username);
//        List<UmsMember> memberList = memberMapper.selectByExample(example);
//        if (!CollectionUtils.isEmpty(memberList)) {
//            return memberList.get(0);
//        }
        CommonResult<UmsMember> umsMemberCommonResult = umsMemberFeignService.loadUserByUsername(username);
        return umsMemberCommonResult.getData();
    }
}
