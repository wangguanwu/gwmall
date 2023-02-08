package com.gw.gwmall.ordercurrent.mapper;

import com.gw.gwmall.ordercurrent.dto.OmsOrderReturnApplyResult;
import com.gw.gwmall.ordercurrent.dto.OmsReturnApplyQueryParam;
import com.gw.gwmall.ordercurrent.model.OmsOrderReturnApply;
import com.gw.gwmall.ordercurrent.model.OmsOrderReturnApplyExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OmsOrderReturnApplyMapper {
    long countByExample(OmsOrderReturnApplyExample example);

    int deleteByExample(OmsOrderReturnApplyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsOrderReturnApply record);

    int insertSelective(OmsOrderReturnApply record);

    List<OmsOrderReturnApply> selectByExample(OmsOrderReturnApplyExample example);

    OmsOrderReturnApply selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsOrderReturnApply record, @Param("example") OmsOrderReturnApplyExample example);

    int updateByExample(@Param("record") OmsOrderReturnApply record, @Param("example") OmsOrderReturnApplyExample example);

    int updateByPrimaryKeySelective(OmsOrderReturnApply record);

    int updateByPrimaryKey(OmsOrderReturnApply record);

    /**
     * 查询申请列表
     */
    @Select("<script>" +
            " SELECT" +
            "        id," +
            "        create_time," +
            "        member_username," +
            "        product_real_price," +
            "        product_count," +
            "        return_name," +
            "        status," +
            "        handle_time" +
            "        FROM" +
            "        oms_order_return_apply" +
            "        WHERE" +
            "        1 = 1" +
            "        <if test=\"queryParam.id!=null\">" +
            "            AND id = #{queryParam.id}" +
            "        </if>" +
            "        <if test=\"queryParam.status!=null\">" +
            "            AND status = #{queryParam.status}" +
            "        </if>" +
            "        <if test=\"queryParam.status!=null\">" +
            "            AND status = #{queryParam.status}" +
            "        </if>" +
            "        <if test=\"queryParam.handleMan!=null and queryParam.handleMan!=''\">" +
            "            AND handle_man = #{queryParam.handleMan}" +
            "        </if>" +
            "        <if test=\"queryParam.createTime!=null and queryParam.createTime!=''\">" +
            "            AND create_time LIKE CONCAT(#{queryParam.createTime}, '%')" +
            "        </if>" +
            "        <if test=\"queryParam.handleTime!=null and queryParam.handleTime!=''\">" +
            "            AND handle_time LIKE CONCAT(#{queryParam.handleTime}, '%')" +
            "        </if>" +
            "        <if test=\"queryParam.receiverKeyword!=null and queryParam.receiverKeyword!=''\">" +
            "            AND (return_name LIKE concat(\"%\",#{queryParam.receiverKeyword},\"%\")" +
            "            OR return_phone LIKE concat(\"%\",#{queryParam.receiverKeyword},\"%\"))" +
            "        </if>" +
            "</script>")
    List<OmsOrderReturnApply> getList(@Param("queryParam") OmsReturnApplyQueryParam queryParam);

    /**
     * 获取申请详情
     */
    @Select("<script>" +
            "SELECT" +
            "            ra.*, ca.id ca_id," +
            "                  ca.address_name ca_address_name," +
            "                  ca.`name` ca_name," +
            "                  ca.phone ca_phone," +
            "                  ca.province ca_province," +
            "                  ca.city ca_city," +
            "                  ca.region ca_region," +
            "                  ca.detail_address ca_detail_address" +
            "        FROM" +
            "            oms_order_return_apply ra" +
            "            LEFT JOIN oms_company_address ca ON ra.company_address_id = ca.id" +
            "        WHERE ra.id=#{id};" +
            "</script>")
    OmsOrderReturnApplyResult getDetail(@Param("id")Long id);
}