package com.gw.gwmall.service.impl;


import com.gw.gwmall.dao.PortalMemberInfoDao;
import com.gw.gwmall.domain.PortalMemberInfo;
import com.gw.gwmall.service.UmsMemberCenterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**

 **/
@Service
public class UmsMemberCenterServiceImpl implements UmsMemberCenterService {

    @Autowired
    private PortalMemberInfoDao portalMemberInfoDao;

    /**
     * 查询会员信息
     * @param memberId
     * @return
     */
    @Override
    public PortalMemberInfo getMemberInfo(Long memberId) {
        return portalMemberInfoDao.getMemberInfo(memberId);
    }
}
