package com.gw.gwmall.ordercurrent.dto;

import com.gw.gwmall.ordercurrent.model.OmsCompanyAddress;
import com.gw.gwmall.ordercurrent.model.OmsOrderReturnApply;
import lombok.Getter;
import lombok.Setter;

/**
 * 申请信息封装
 */
public class OmsOrderReturnApplyResult extends OmsOrderReturnApply {
    @Getter
    @Setter
    private OmsCompanyAddress companyAddress;
}
