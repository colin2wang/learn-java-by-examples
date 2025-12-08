package com.colin.java.market.oms.strategy;

import com.colin.java.market.oms.order.Order;
import com.colin.java.market.oms.order.OrderBook;

/**
 * 撮合策略接口
 * 定义不同订单类型的撮合行为
 */
public interface MatchingStrategy {
    
    /**
     * 执行撮合
     * @param order 待撮合的订单
     * @param orderBook 订单簿
     * @return 是否撮合成功或部分成功
     */
    boolean match(Order order, OrderBook orderBook);
    
    /**
     * 获取策略名称
     * @return 策略名称
     */
    String getName();
    
    /**
     * 获取策略描述
     * @return 策略描述
     */
    String getDescription();
}