package com.gw.gwmall.ordercurrent.mapper;

import com.gw.gwmall.ordercurrent.model.OmsOrderOperateHistory;
import com.gw.gwmall.ordercurrent.model.OmsOrderOperateHistoryExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OmsOrderOperateHistoryMapper {
    long countByExample(OmsOrderOperateHistoryExample example);

    int deleteByExample(OmsOrderOperateHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderOperateHistory record);

    int insertSelective(OmsOrderOperateHistory record);

    List<OmsOrderOperateHistory> selectByExample(OmsOrderOperateHistoryExample example);

    OmsOrderOperateHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsOrderOperateHistory record, @Param("example") OmsOrderOperateHistoryExample example);

    int updateByExample(@Param("record") OmsOrderOperateHistory record, @Param("example") OmsOrderOperateHistoryExample example);

    int updateByPrimaryKeySelective(OmsOrderOperateHistory record);

    int updateByPrimaryKey(OmsOrderOperateHistory record);

    @Select("<script>" +
            "INSERT INTO oms_order_operate_history (order_id, operate_man, create_time, order_status, note) VALUES" +
            "        <foreach collection=\"list\" separator=\",\" item=\"item\" index=\"index\">" +
            "            (#{item.orderId}," +
            "            #{item.operateMan}," +
            "            #{item.createTime,jdbcType=TIMESTAMP}," +
            "            #{item.orderStatus}," +
            "            #{item.note})" +
            "        </foreach>" +
            "</script>")
    int insertList(@Param("list") List<OmsOrderOperateHistory> orderOperateHistoryList);
}