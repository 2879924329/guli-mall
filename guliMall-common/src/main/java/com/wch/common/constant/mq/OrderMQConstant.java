package com.wch.common.constant.mq;

/**
 * @author wch
 * @version 1.0
 * @date 2022/4/6 19:55
 */
public class OrderMQConstant {
    public static final String ORDER_RELEASE_ORDER_QUEUE = "order.release.order.queue";
    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";

    public static final String ORDER_CREATE_ORDER_ROUTE_KRY = "order.create.order";
    public static final String ORDER_RELEASE_OTHER_ROUTE_KEY = "order.release.other.#";
}
