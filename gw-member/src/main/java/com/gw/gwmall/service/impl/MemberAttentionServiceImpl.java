package com.gw.gwmall.service.impl;


import com.gw.gwmall.domain.MemberBrandAttention;
import com.gw.gwmall.repository.MemberBrandAttentionRepository;
import com.gw.gwmall.service.MemberAttentionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员关注Service实现类
 */
@Service
public class MemberAttentionServiceImpl implements MemberAttentionService {
    @Autowired
    private MemberBrandAttentionRepository memberBrandAttentionRepository;

    @Override
    public int add(MemberBrandAttention memberBrandAttention) {
        int count = 0;
        MemberBrandAttention findAttention = memberBrandAttentionRepository.findByMemberIdAndBrandId(memberBrandAttention.getMemberId(), memberBrandAttention.getBrandId());
        if (findAttention == null) {
            memberBrandAttentionRepository.save(memberBrandAttention);
            count = 1;
        }
        return count;
    }

    @Override
    public int delete(Long memberId, Long brandId) {
        return memberBrandAttentionRepository.deleteByMemberIdAndBrandId(memberId,brandId);
    }

    @Override
    public List<MemberBrandAttention> list(Long memberId) {
        return memberBrandAttentionRepository.findByMemberId(memberId);
    }
}
