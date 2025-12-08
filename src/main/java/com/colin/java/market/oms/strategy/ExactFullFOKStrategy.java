package com.colin.java.market.oms.strategy;

import com.colin.java.market.oms.order.Order;
import com.colin.java.market.oms.order.OrderBook;
import com.colin.java.market.oms.order.Side;

import java.util.PriorityQueue;

/**
 * Exact-Full-FOK 策略实现
 * "要么全成，要么全废，价格必须打平。"
 * 价格严格相等；数量必须一次满足；未成交部分整单取消。
 */
public class ExactFullFOKStrategy extends AbstractMatchingStrategy {
    
    public ExactFullFOKStrategy() {
        super("EFF", "要么全成，要么全废，价格必须打平。价格严格相等；数量必须一次满足；未成交部分整单取消。");
    }
    
    @Override
    public boolean match(Order order, OrderBook orderBook) {
        PriorityQueue<Order> matchQueue = order.side == Side.BUY ? orderBook.sellOrders : orderBook.buyOrders;
        
        // 检查是否有完全匹配的订单
        for (Order bookOrder : matchQueue) {
            // 价格必须严格相等
            if (isExactPriceMatch(order.price, bookOrder.price)) {
                // 数量必须完全匹配
                if (order.quantity == bookOrder.quantity) {
                    // 执行匹配
                    System.out.println("[EFF策略] 完全匹配: " + order + " 与 " + bookOrder);
                    matchQueue.remove(bookOrder);
                    return true;
                }
            }
        }
        
        // 没有找到完全匹配的订单，整单取消
        System.out.println("[EFF策略] 未找到完全匹配订单，整单取消: " + order);
        return false;
    }
}