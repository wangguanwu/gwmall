package com.gw.gwmall.domain;

import com.gw.gwmall.model.UmsMember;
import com.gw.gwmall.model.UmsMemberLevel;

import lombok.Data;

/**
 * @date ：Created in 2020/1/6 21:12

 * @description:
 **/
@Data
public class PortalMemberInfo extends UmsMember {
    private UmsMemberLevel umsMemberLevel;
}
