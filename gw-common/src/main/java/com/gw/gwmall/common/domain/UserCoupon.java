package com.gw.gwmall.common.domain;

import lombok.Data;

/**
 * @author guanwu
 * @created on 2023-02-16 11:44:36
 **/

@Data
public class UserCoupon {
    private Long couponId;
    private Integer type;
    private String nick;
    private Long memberId;

}
