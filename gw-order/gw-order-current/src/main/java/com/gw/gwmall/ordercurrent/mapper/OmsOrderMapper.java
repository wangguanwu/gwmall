package com.gw.gwmall.ordercurrent.mapper;

import com.gw.gwmall.ordercurrent.domain.OmsOrderDetail;
import com.gw.gwmall.ordercurrent.dto.OmsOrderDeliveryParam;
import com.gw.gwmall.ordercurrent.dto.OmsOrderQueryParam;
import com.gw.gwmall.ordercurrent.model.OmsOrder;
import com.gw.gwmall.ordercurrent.model.OmsOrderExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface OmsOrderMapper{
    long countByExample(OmsOrderExample example);

    int deleteByExample(OmsOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrder record);

    int insertSelective(OmsOrder record);

    List<OmsOrder> selectByExample(OmsOrderExample example);

    OmsOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsOrder record, @Param("example") OmsOrderExample example);

    int updateByExample(@Param("record") OmsOrder record, @Param("example") OmsOrderExample example);

    int updateByPrimaryKeySelective(OmsOrder record);

    int updateByPrimaryKey(OmsOrder record);

    /**
     * 条件查询订单
     */
    @Select("<script>" +
            "SELECT *" +
            "        FROM" +
            "        oms_order" +
            "        WHERE" +
            "        delete_status = 0" +
            "        <if test=\"queryParam.orderSn!=null and queryParam.orderSn!=''\">" +
            "            AND order_sn = #{queryParam.orderSn}" +
            "        </if>" +
            "        <if test=\"queryParam.memberId!=null and queryParam.memberId!=''\">" +
            "            AND member_id = #{queryParam.memberId}" +
            "        </if>" +
            "        <if test=\"queryParam.status!=null\">" +
            "            AND `status` = #{queryParam.status}" +
            "        </if>" +
            "        <if test=\"queryParam.sourceType!=null\">" +
            "            AND source_type = #{queryParam.sourceType}" +
            "        </if>" +
            "        <if test=\"queryParam.orderType!=null\">" +
            "            AND order_type = #{queryParam.orderType}" +
            "        </if>" +
            "        <if test=\"queryParam.createTime!=null and queryParam.createTime!=''\">" +
            "            AND create_time LIKE concat(#{queryParam.createTime},\"%\")" +
            "        </if>" +
            "        <if test=\"queryParam.receiverKeyword!=null and queryParam.receiverKeyword!=''\">" +
            "            AND (" +
            "            receiver_name LIKE concat(\"%\",#{queryParam.receiverKeyword},\"%\")" +
            "            OR receiver_phone LIKE concat(\"%\",#{queryParam.receiverKeyword},\"%\")" +
            "            )" +
            "        </if>" +
            "</script>")
    List<OmsOrder> getList(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 批量发货
     */
    @Update("<script>" +
            "UPDATE oms_order" +
            "        SET" +
            "        delivery_sn = CASE id" +
            "        <foreach collection=\"list\" item=\"item\">" +
            "            WHEN #{item.orderId} THEN #{item.deliverySn}" +
            "        </foreach>" +
            "        END," +
            "        delivery_company = CASE id" +
            "        <foreach collection=\"list\" item=\"item\">" +
            "            WHEN #{item.orderId} THEN #{item.deliveryCompany}" +
            "        </foreach>" +
            "        END," +
            "        delivery_time = CASE id" +
            "        <foreach collection=\"list\" item=\"item\">" +
            "            WHEN #{item.orderId} THEN now()" +
            "        </foreach>" +
            "        END," +
            "        `status` = CASE id" +
            "        <foreach collection=\"list\" item=\"item\">" +
            "            WHEN #{item.orderId} THEN 2" +
            "        </foreach>" +
            "        END" +
            "        WHERE" +
            "        id IN" +
            "        <foreach collection=\"list\" item=\"item\" separator=\",\" open=\"(\" close=\")\">" +
            "            #{item.orderId}" +
            "        </foreach>" +
            "        AND `status` = 1" +
            "</script>")
    int delivery(@Param("list") List<OmsOrderDeliveryParam> deliveryParamList);

    /**
     * 获取订单详情
     */
    @Select("<script>" +
            "SELECT o.*," +
            "            oi.id item_id," +
            "            oi.product_id item_product_id," +
            "            oi.product_sn item_product_sn," +
            "            oi.product_pic item_product_pic," +
            "            oi.product_name item_product_name," +
            "            oi.product_brand item_product_brand," +
            "            oi.product_price item_product_price," +
            "            oi.product_quantity item_product_quantity," +
            "            oi.product_attr item_product_attr," +
            "            oi.sp1 item_sp1," +
            "            oi.sp2 item_sp2," +
            "            oi.sp3 item_sp3," +
            "            oh.id history_id," +
            "            oh.operate_man history_operate_man," +
            "            oh.create_time history_create_time," +
            "            oh.order_status history_order_status," +
            "            oh.note history_note" +
            "        FROM" +
            "            oms_order o" +
            "            LEFT JOIN oms_order_item oi ON o.id = oi.order_id" +
            "            LEFT JOIN oms_order_operate_history oh ON o.id = oh.order_id" +
            "        WHERE" +
            "            o.id = #{id}" +
            "        ORDER BY oi.id ASC,oh.create_time DESC" +
            "</script>")
    OmsOrderDetail getDetail(@Param("id") Long id);
}