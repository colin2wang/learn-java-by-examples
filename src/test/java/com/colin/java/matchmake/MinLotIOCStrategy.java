package com.colin.java.matchmake;

import java.math.BigDecimal;
import java.util.PriorityQueue;

/**
 * Min-Lot-IOC 策略实现
 * "最小手数以上能成多少算多少，剩的立即撤。"
 * 先按 Best-Price+FIFO 撮合；剩余数量＜Min-Lot 时自动撤单。
 */
public class MinLotIOCStrategy extends AbstractMatchingStrategy {
    
    public MinLotIOCStrategy() {
        super("MLI", "最小手数以上能成多少算多少，剩的立即撤。先按 Best-Price+FIFO 撮合；剩余数量＜Min-Lot 时自动撤单。");
    }
    
    @Override
    public boolean match(Order order, OrderBook orderBook) {
        PriorityQueue<Order> matchQueue = order.side == Side.BUY ? orderBook.sellOrders : orderBook.buyOrders;
        boolean hasMatched = false;
        long remainingQuantity = order.quantity;
        
        // 使用临时副本进行迭代，避免ConcurrentModificationException
        PriorityQueue<Order> tempQueue = new PriorityQueue<>(matchQueue);
        
        for (Order bookOrder : tempQueue) {
            // 价格匹配
            if (isPriceMatch(order.side == Side.BUY ? order.price : bookOrder.price, 
                           order.side == Side.BUY ? bookOrder.price : order.price)) {
                
                // 计算成交数量
                long tradeQuantity = Math.min(remainingQuantity, bookOrder.quantity);
                
                System.out.println("[MLI策略] 部分匹配: " + order + " 与 " + bookOrder + ", 成交数量: " + tradeQuantity);
                
                // 减少订单簿中订单的数量
                bookOrder.quantity -= tradeQuantity;
                remainingQuantity -= tradeQuantity;
                hasMatched = true;
                
                // 如果订单簿中的订单完全成交，从队列中移除
                if (bookOrder.quantity == 0) {
                    matchQueue.remove(bookOrder);
                }
                
                // 如果当前订单已经完全成交，结束匹配
                if (remainingQuantity == 0) {
                    break;
                }
            }
        }
        
        // 检查剩余数量是否小于最小手数
        if (remainingQuantity > 0) {
            if (remainingQuantity < order.minLot) {
                System.out.println("[MLI策略] 剩余数量 " + remainingQuantity + " 小于最小手数 " + order.minLot + "，自动撤单");
            } else {
                System.out.println("[MLI策略] 剩余数量 " + remainingQuantity + " 未成交，但大于等于最小手数，按IOC规则撤单");
            }
        }
        
        return hasMatched;
    }
}