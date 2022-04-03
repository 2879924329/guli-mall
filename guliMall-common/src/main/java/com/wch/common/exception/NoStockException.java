package com.wch.common.exception;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/3 20:10
 */
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId) {
        super("商品id："+skuId+"无库存！");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
