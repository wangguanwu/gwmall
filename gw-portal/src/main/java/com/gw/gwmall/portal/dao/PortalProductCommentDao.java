package com.gw.gwmall.portal.dao;

import com.gw.gwmall.portal.domain.PmsCommentParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**

 * @description: 
 **/
public interface PortalProductCommentDao {

    List<PmsCommentParam> getCommentList(Long productId);

    Integer selectUserOrder(@Param("userId") Long userId, @Param("productId") Long productId);
}
