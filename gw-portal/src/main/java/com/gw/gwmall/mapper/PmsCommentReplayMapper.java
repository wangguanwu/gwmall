package com.gw.gwmall.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.gw.gwmall.model.PmsCommentReplay;
import com.gw.gwmall.model.PmsCommentReplayExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("goods")
public interface PmsCommentReplayMapper {
    long countByExample(PmsCommentReplayExample example);

    int deleteByExample(PmsCommentReplayExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsCommentReplay record);

    int insertSelective(PmsCommentReplay record);

    List<PmsCommentReplay> selectByExample(PmsCommentReplayExample example);

    PmsCommentReplay selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsCommentReplay record, @Param("example") PmsCommentReplayExample example);

    int updateByExample(@Param("record") PmsCommentReplay record, @Param("example") PmsCommentReplayExample example);

    int updateByPrimaryKeySelective(PmsCommentReplay record);

    int updateByPrimaryKey(PmsCommentReplay record);
}