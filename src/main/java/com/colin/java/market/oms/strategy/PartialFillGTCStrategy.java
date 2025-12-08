package com.colin.java.market.oms.strategy;

import com.colin.java.market.oms.order.Order;
import com.colin.java.market.oms.order.OrderBook;
import com.colin.java.market.oms.order.Side;

import java.util.PriorityQueue;

/**
 * Partial-Fill-GTC 策略实现
 * "允许零头，慢慢排队，直到完成为止。"
 * 价格 Best-Price；可部分成交；尾巴 GTC 留在订单簿继续排队。
 */
public class PartialFillGTCStrategy extends AbstractMatchingStrategy {
    
    public PartialFillGTCStrategy() {
        super("PFG", "允许零头，慢慢排队，直到完成为止。价格 Best-Price；可部分成交；尾巴 GTC 留在订单簿继续排队。");
    }
    
    @Override
    public boolean match(Order order, OrderBook orderBook) {
        PriorityQueue<Order> matchQueue = order.side == Side.BUY ? orderBook.sellOrders : orderBook.buyOrders;
        PriorityQueue<Order> restingQueue = order.side == Side.BUY ? orderBook.buyOrders : orderBook.sellOrders;
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
                
                System.out.println("[PFG策略] 部分匹配: " + order + " 与 " + bookOrder + ", 成交数量: " + tradeQuantity);
                
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
            } else {
                // 价格不匹配，结束当前匹配过程
                break;
            }
        }
        
        // 如果有剩余数量，将剩余部分加入订单簿排队
        if (remainingQuantity > 0) {
            Order remainingOrder = new Order(order.side, order.price.doubleValue(), remainingQuantity, order.orderType);
            remainingOrder.minLot = order.minLot;
            remainingOrder.maxRetries = order.maxRetries;
            restingQueue.add(remainingOrder);
            System.out.println("[PFG策略] 将剩余数量 " + remainingQuantity + " 加入订单簿排队: " + remainingOrder);
        }
        
        return hasMatched;
    }
}