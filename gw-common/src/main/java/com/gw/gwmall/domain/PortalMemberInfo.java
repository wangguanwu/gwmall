package com.gw.gwmall.domain;

import com.gw.gwmall.model.UmsMember;
import com.gw.gwmall.model.UmsMemberLevel;
import lombok.Data;

/**
 * @description:
 **/
@Data
public class PortalMemberInfo extends UmsMember {
    private UmsMemberLevel umsMemberLevel;
}
