package com.gw.gwmall.common.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品变更参数
 *
 * @author guanwu
 * @created on 2023-03-10 12:37:13
 **/

@Data
public class ESProductUpdateParam implements Serializable {
    private static final long serialVersionUID = -5386453819467012333L;
    private Integer type;
    private Long id;

    public ESProductUpdateParam() {}

    public ESProductUpdateParam(Long id, Integer type) {
        this.type = type;
        this.id = id;
    }
    
    public static final int CHANGE_TYPE_ADD = 0;
    public static final int CHANGE_TYPE_UPDATE = 1;
    public static final int CHANGE_TYPE_DELETE = 2;
}
