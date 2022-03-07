package com.wch.common.constant;

/**
 * @author wch
 * @version 1.0
 * @date 2022/3/7 14:17
 */
public class WareConstant {
    public enum PurchaseStatusEnum {
        CREATED(0, "新建"), ASSIGNED(1, "已分配"),
        RECEIVED(2, "已领取"),
        FINISHED(3, "已完成"),
        HASERROR(4, "有异常");

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
    public enum PurchaseDetailStatusEnum {
        CREATED(0, "新建"),
        WAIT_ASSIGNED(1, "待分配"),
        ASSIGNED(2, "已分配"),
        BUYING(3, "正在采购"),
        FINISHED(4, "已完成"),
        HASERROR(5, "采购失败");

        PurchaseDetailStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
