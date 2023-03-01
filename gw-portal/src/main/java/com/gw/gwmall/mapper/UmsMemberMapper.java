package com.gw.gwmall.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.gw.gwmall.model.UmsMember;
import com.gw.gwmall.model.UmsMemberExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("user")
public interface UmsMemberMapper {
    long countByExample(UmsMemberExample example);

    int deleteByExample(UmsMemberExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMember record);

    int insertSelective(UmsMember record);

    List<UmsMember> selectByExample(UmsMemberExample example);

    UmsMember selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UmsMember record, @Param("example") UmsMemberExample example);

    int updateByExample(@Param("record") UmsMember record, @Param("example") UmsMemberExample example);

    int updateByPrimaryKeySelective(UmsMember record);

    int updateByPrimaryKey(UmsMember record);
}