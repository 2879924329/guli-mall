package com.wch.common.constant.mq;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/5 20:13
 */
public class StockMQConstant {
    public static final String STOCK_EVENT_EXCHANGE = "stock-event-exchange";
    public static final String STOCK_RELEASE_STOCK_QUEUE = "stock.release.stock.queue";
    public static final String STOCK_DELAY_QUEUE = "stock.delay.queue";
    public static final String STOCK_LOCKED_ROUTE_KEY = "stock.locked";
    public static final String STOCK_RELEASE_ROUTE_KEY= "stock.release.#";

}
