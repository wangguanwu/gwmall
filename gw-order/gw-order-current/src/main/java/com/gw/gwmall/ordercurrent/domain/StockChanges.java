package com.gw.gwmall.ordercurrent.domain;

public class StockChanges {

    private Long productSkuId;

    private Integer changesCount;

    public Long getProductSkuId() {
        return productSkuId;
    }

    public void setProductSkuId(Long productSkuId) {
        this.productSkuId = productSkuId;
    }

    public Integer getChangesCount() {
        return changesCount;
    }

    public void setChangesCount(Integer changesCount) {
        this.changesCount = changesCount;
    }

    public StockChanges(){}

    public StockChanges(Long productSkuId, Integer changesCount) {
        this.productSkuId = productSkuId;
        this.changesCount = changesCount;
    }
}
