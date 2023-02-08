package com.gw.gwmall.ordercurrent.service.impl;

import com.github.pagehelper.PageHelper;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderReturnReasonMapper;
import com.gw.gwmall.ordercurrent.model.OmsOrderReturnReason;
import com.gw.gwmall.ordercurrent.model.OmsOrderReturnReasonExample;
import com.gw.gwmall.ordercurrent.service.OmsOrderReturnReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单原因管理Service实现类
 */
@Service
public class OmsOrderReturnReasonServiceImpl implements OmsOrderReturnReasonService {
    @Autowired
    private OmsOrderReturnReasonMapper returnReasonMapper;
    @Override
    public int create(OmsOrderReturnReason returnReason) {
        returnReason.setCreateTime(new Date());
        return returnReasonMapper.insert(returnReason);
    }

    @Override
    public int update(Long id, OmsOrderReturnReason returnReason) {
        returnReason.setId(id);
//        return returnReasonMapper.updateById(returnReason);
        return returnReasonMapper.updateByPrimaryKeySelective(returnReason);
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.createCriteria().andIdIn(ids);
        return returnReasonMapper.deleteByExample(example);
//        return returnReasonMapper.deleteBatchIds(ids);
    }

    @Override
    public List<OmsOrderReturnReason> list(Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
//        QueryWrapper<OmsOrderReturnReason> queryWrapper = new QueryWrapper<>();
//        queryWrapper.orderByDesc("create_time");
//        return returnReasonMapper.selectList(queryWrapper);

        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.setOrderByClause("create_time DESC");
        return returnReasonMapper.selectByExample(example);
    }

    @Override
    public int updateStatus(List<Long> ids, Integer status) {
        if(!status.equals(0)&&!status.equals(1)){
            return 0;
        }
        OmsOrderReturnReason record = new OmsOrderReturnReason();
        record.setStatus(status);
//        UpdateWrapper<OmsOrderReturnReason> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.in("id",ids);
//        return returnReasonMapper.update(record,updateWrapper);

        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.createCriteria().andIdIn(ids);
        return returnReasonMapper.updateByExample(record,example);
    }

    @Override
    public OmsOrderReturnReason getItem(Long id) {
        return returnReasonMapper.selectByPrimaryKey(id);
    }
}
