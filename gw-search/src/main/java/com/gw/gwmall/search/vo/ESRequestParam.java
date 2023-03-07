package com.gw.gwmall.search.vo;



import java.util.Date;
import java.util.List;

/**
 * @author guanwu
 */
public class ESRequestParam {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 品牌id,可以多选
     */
    private List<Long> brandId;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 排序条件：sort=price/salecount/hotscore_desc/asc
     */
    private String sort;

    private Long salecount;//销量

    private Date putawayDate;//上架时间
    /**
     * 是否显示有货  1代表有货
     */
    private Integer hasStock;

    /**
     * 价格区间查询
     */
    private String price;

    /**
     * 按照属性进行筛选
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String queryString;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Long> getBrandId() {
        return brandId;
    }

    public void setBrandId(List<Long> brandId) {
        this.brandId = brandId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getHasStock() {
        return hasStock;
    }

    public void setHasStock(Integer hasStock) {
        this.hasStock = hasStock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public List<String> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<String> attrs) {
        this.attrs = attrs;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSalecount() {
        return salecount;
    }

    public void setSalecount(Long salecount) {
        this.salecount = salecount;
    }

    public Date getPutawayDate() {
        return putawayDate;
    }

    public void setPutawayDate(Date putawayDate) {
        this.putawayDate = putawayDate;
    }
}
