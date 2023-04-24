package com.gw.gwmall.ordercurrent.mapper;

import com.gw.gwmall.ordercurrent.model.OmsOrderCancelMessageHistory;
import com.gw.gwmall.ordercurrent.model.OmsOrderCancelMessageHistoryExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OmsOrderCancelMessageHistoryMapper {
    long countByExample(OmsOrderCancelMessageHistoryExample example);

    int deleteByExample(OmsOrderCancelMessageHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderCancelMessageHistory row);

    int insertSelective(OmsOrderCancelMessageHistory row);

    List<OmsOrderCancelMessageHistory> selectByExample(OmsOrderCancelMessageHistoryExample example);

    OmsOrderCancelMessageHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") OmsOrderCancelMessageHistory row, @Param("example") OmsOrderCancelMessageHistoryExample example);

    int updateByExample(@Param("row") OmsOrderCancelMessageHistory row, @Param("example") OmsOrderCancelMessageHistoryExample example);

    int updateByPrimaryKeySelective(OmsOrderCancelMessageHistory row);

    int updateByPrimaryKey(OmsOrderCancelMessageHistory row);
}