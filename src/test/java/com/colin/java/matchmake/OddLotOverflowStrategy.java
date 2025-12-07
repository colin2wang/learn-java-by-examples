package com.colin.java.matchmake;

import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;

/**
 * Odd-Lot-Overflow 策略实现
 * "碎股先吃，吃不掉扔回 Overflow 队列重试。"
 * 允许碎股成交；本轮未成交部分进入 Overflow-Requeue，下一周期再撮合；可配置最大重试次数。
 */
public class OddLotOverflowStrategy extends AbstractMatchingStrategy {
    // 溢出队列
    private final Queue<OrderWithRetry> overflowQueue = new LinkedList<>();
    
    public OddLotOverflowStrategy() {
        super("OLO", "碎股先吃，吃不掉扔回 Overflow 队列重试。允许碎股成交；本轮未成交部分进入 Overflow-Requeue，下一周期再撮合；可配置最大重试次数。");
    }
    
    @Override
    public boolean match(Order order, OrderBook orderBook) {
        // 先处理溢出队列中的订单
        processOverflowQueue(orderBook);
        
        // 处理当前订单
        boolean hasMatched = processOrder(order, orderBook);
        
        return hasMatched;
    }
    
    private void processOverflowQueue(OrderBook orderBook) {
        Queue<OrderWithRetry> newOverflowQueue = new LinkedList<>();
        
        while (!overflowQueue.isEmpty()) {
            OrderWithRetry retryOrder = overflowQueue.poll();
            
            // 检查是否超过最大重试次数
            if (retryOrder.retryCount >= retryOrder.order.maxRetries) {
                System.out.println("[OLO策略] 订单超过最大重试次数，取消: " + retryOrder.order);
                continue;
            }
            
            // 增加重试计数
            retryOrder.retryCount++;
            System.out.println("[OLO策略] 从溢出队列重试订单: " + retryOrder.order + "，重试次数: " + retryOrder.retryCount);
            
            // 尝试撮合
            boolean matched = processOrder(retryOrder.order, orderBook);
            
            // 如果仍然有剩余数量，重新加入溢出队列
            if (retryOrder.order.quantity > 0) {
                newOverflowQueue.add(retryOrder);
            }
        }
        
        // 更新溢出队列
        overflowQueue.addAll(newOverflowQueue);
    }
    
    private boolean processOrder(Order order, OrderBook orderBook) {
        PriorityQueue<Order> matchQueue = order.side == Side.BUY ? orderBook.sellOrders : orderBook.buyOrders;
        boolean hasMatched = false;
        long remainingQuantity = order.quantity;
        
        // 使用临时副本进行迭代
        PriorityQueue<Order> tempQueue = new PriorityQueue<>(matchQueue);
        
        for (Order bookOrder : tempQueue) {
            // 价格匹配
            if (isPriceMatch(order.side == Side.BUY ? order.price : bookOrder.price,
                           order.side == Side.BUY ? bookOrder.price : order.price)) {
                
                // 允许碎股成交，计算实际成交数量
                long tradeQuantity = Math.min(remainingQuantity, bookOrder.quantity);
                
                System.out.println("[OLO策略] 碎股匹配: " + order + " 与 " + bookOrder + ", 成交数量: " + tradeQuantity);
                
                // 减少订单数量
                bookOrder.quantity -= tradeQuantity;
                remainingQuantity -= tradeQuantity;
                hasMatched = true;
                
                // 移除完全成交的订单
                if (bookOrder.quantity == 0) {
                    matchQueue.remove(bookOrder);
                }
                
                // 如果当前订单已经完全成交，结束匹配
                if (remainingQuantity == 0) {
                    break;
                }
            }
        }
        
        // 更新原订单的剩余数量
        order.quantity = remainingQuantity;
        
        // 如果有剩余数量，加入溢出队列
        if (remainingQuantity > 0 && order.maxRetries > 0) {
            overflowQueue.add(new OrderWithRetry(order));
            System.out.println("[OLO策略] 订单剩余数量 " + remainingQuantity + " 加入溢出队列: " + order);
        }
        
        return hasMatched;
    }
    
    // 包装订单和重试计数的内部类
    private static class OrderWithRetry {
        Order order;
        int retryCount;
        
        OrderWithRetry(Order order) {
            this.order = order;
            this.retryCount = 0;
        }
    }
}