package com.gw.gwmall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.mapper.PmsCommentMapper;
import com.gw.gwmall.mapper.PmsCommentReplayMapper;
import com.gw.gwmall.model.PmsComment;
import com.gw.gwmall.model.PmsCommentReplay;
import com.gw.gwmall.model.UmsMember;
import com.gw.gwmall.portal.dao.PortalProductCommentDao;
import com.gw.gwmall.portal.domain.PmsCommentParam;
import com.gw.gwmall.portal.feignapi.ums.UmsMemberFeignApi;
import com.gw.gwmall.portal.service.PortalProductCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @description:
 **/
@Service
public class PortalProductCommentServiceImpl implements PortalProductCommentService {
    @Autowired
    private PortalProductCommentDao productCommentDao;

    @Autowired
    private PmsCommentMapper pmsMapper;

    @Autowired
    private PmsCommentReplayMapper replayMapper;

    @Autowired
    private UmsMemberFeignApi umsMemberFeignApi;

    /**
     * 获取评论列表
     * @param productId
     * @return
     */
    @Override
    public CommonResult<List<PmsCommentParam>> getCommentList(Long productId, Integer pageNum, Integer pageSize) {
        //todo  通过feign远程调用
        PageHelper.startPage(pageNum,pageSize);
        return CommonResult.success(productCommentDao.getCommentList(productId));
    }

    /**
     * 用户评价
     * @param pmsComment
     * @return
     */
    @Override
    public Integer insertProductComment(PmsComment pmsComment) {
        UmsMember member = umsMemberFeignApi.getMemberById().getData();
        //判断一下当前用户是否购买过当前评论的商品
        //todo 通过openfeign远程调用
        Integer status = productCommentDao.selectUserOrder(member.getId(), pmsComment.getProductId());
        if(status > 0){
            pmsComment.setCreateTime(new Date());
            pmsComment.setShowStatus(0);
            pmsComment.setMemberNickName(member.getNickname());
            pmsComment.setMemberIcon(member.getIcon());
            return pmsMapper.insert(pmsComment);
        }
        return -1;
    }

    /**
     * 用户评价回复
     * @param reply
     * @return
     */
    @Override
    public Integer insertCommentReply(PmsCommentReplay reply) {
        //todo 通过feign远程调用| 服务拆到商品服务中
        UmsMember member = umsMemberFeignApi.getMemberById().getData();
        reply.setCreateTime(new Date());
        reply.setMemberNickName(member.getNickname());
        reply.setMemberIcon(member.getIcon());
        reply.setType(0);
        return replayMapper.insert(reply);
    }
}
