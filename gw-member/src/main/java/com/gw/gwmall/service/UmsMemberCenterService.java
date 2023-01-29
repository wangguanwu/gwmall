package com.gw.gwmall.service;


import com.gw.gwmall.domain.PortalMemberInfo;

/**
 * @author ：图灵学院

 **/
public interface UmsMemberCenterService {

    /**
     * 查询会员信息
     * @param memberId
     * @return
     */
    PortalMemberInfo getMemberInfo(Long memberId);
}
