package com.gw.gwmall.search.domain;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 搜索中的商品属性信息
 */
public class EsProductAttributeValue implements Serializable {
    private static final long serialVersionUID = 1L;
    @Field(type = FieldType.Long)
    private Long id;
    @Field(type = FieldType.Long)
    private Long attrId;
    @Field(type = FieldType.Keyword)
    private String attrName;
    //属性值
    @Field(type = FieldType.Keyword)
    private String attrValue;
    //属性参数：0->规格；1->参数
    private Integer attrType;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAttrType() {
        return attrType;
    }

    public void setAttrType(Integer attrType) {
        this.attrType = attrType;
    }
}
