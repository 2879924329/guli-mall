package com.wch.common.exception;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/1 22:05
 *
 * 状态码
 */
public enum Code {
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001, "参数格式校验失败"),
    // 库存模块异常
    WARE_PURCHASE_MERGE_FAILED(11001, "无法将采购项合并到已被领取的采购单"),

    WARE_PURCHASE_ASSIGN_FAILED(11002, "只能给新建的采购单分配采购员"),
    WARE_SKU_STOCK_NOT_ENOUGH(11003, "商品库存不足"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架出现异常");
    private int code;
    private String message;
    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
