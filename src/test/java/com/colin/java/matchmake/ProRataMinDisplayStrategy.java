package com.colin.java.matchmake;

import java.math.BigDecimal;
import java.util.*;

/**
 * Pro-Rata-Min-Display 策略实现
 * "同价档按展示量比例分，每人都得≥最小手数。"
 * 同价档先 Pro-Rata 分配，不足 Min-Lot 的份额向下舍入；余量继续排队。
 */
public class ProRataMinDisplayStrategy extends AbstractMatchingStrategy {
    
    public ProRataMinDisplayStrategy() {
        super("PRM", "同价档按展示量比例分，每人都得≥最小手数。同价档先 Pro-Rata 分配，不足 Min-Lot 的份额向下舍入；余量继续排队。");
    }
    
    @Override
    public boolean match(Order order, OrderBook orderBook) {
        PriorityQueue<Order> matchQueue = order.side == Side.BUY ? orderBook.sellOrders : orderBook.buyOrders;
        boolean hasMatched = false;
        long remainingQuantity = order.quantity;
        
        // 按价格分组订单
        Map<BigDecimal, List<Order>> priceGroups = groupOrdersByPrice(matchQueue, order);
        
        for (Map.Entry<BigDecimal, List<Order>> entry : priceGroups.entrySet()) {
            BigDecimal price = entry.getKey();
            List<Order> samePriceOrders = entry.getValue();
            
            // 计算同价格档的总展示量
            long totalDisplayQuantity = calculateTotalDisplayQuantity(samePriceOrders);
            
            if (totalDisplayQuantity > 0) {
                // Pro-Rata分配
                Map<Order, Long> allocations = allocateProRata(samePriceOrders, remainingQuantity, totalDisplayQuantity, order.minLot);
                
                // 执行分配
                for (Map.Entry<Order, Long> allocEntry : allocations.entrySet()) {
                    Order bookOrder = allocEntry.getKey();
                    long allocatedQuantity = allocEntry.getValue();
                    
                    if (allocatedQuantity > 0) {
                        System.out.println("[PRM策略] 比例分配匹配: " + order + " 与 " + bookOrder + ", 分配数量: " + allocatedQuantity);
                        
                        // 减少订单数量
                        bookOrder.quantity -= allocatedQuantity;
                        remainingQuantity -= allocatedQuantity;
                        hasMatched = true;
                        
                        // 移除完全成交的订单
                        if (bookOrder.quantity == 0) {
                            matchQueue.remove(bookOrder);
                        }
                    }
                }
                
                if (remainingQuantity == 0) {
                    break;
                }
            }
        }
        
        return hasMatched;
    }
    
    private Map<BigDecimal, List<Order>> groupOrdersByPrice(PriorityQueue<Order> queue, Order incomingOrder) {
        Map<BigDecimal, List<Order>> groups = new LinkedHashMap<>();
        
        for (Order order : queue) {
            boolean priceMatch = isPriceMatch(
                incomingOrder.side == Side.BUY ? incomingOrder.price : order.price,
                incomingOrder.side == Side.BUY ? order.price : incomingOrder.price
            );
            
            if (priceMatch) {
                groups.computeIfAbsent(order.price, k -> new ArrayList<>()).add(order);
            }
        }
        
        return groups;
    }
    
    private long calculateTotalDisplayQuantity(List<Order> orders) {
        long total = 0;
        for (Order order : orders) {
            total += order.quantity;
        }
        return total;
    }
    
    private Map<Order, Long> allocateProRata(List<Order> orders, long incomingQuantity, long totalQuantity, long minLot) {
        Map<Order, Long> allocations = new HashMap<>();
        long allocatedTotal = 0;
        
        // 第一阶段：按比例分配
        for (Order order : orders) {
            double ratio = (double) order.quantity / totalQuantity;
            long allocated = (long) Math.floor(ratio * incomingQuantity);
            
            // 确保不低于最小手数
            if (allocated >= minLot) {
                allocations.put(order, allocated);
                allocatedTotal += allocated;
            }
        }
        
        // 第二阶段：处理余量（简单起见，这里不再分配）
        if (allocatedTotal < incomingQuantity) {
            System.out.println("[PRM策略] 未分配的余量: " + (incomingQuantity - allocatedTotal) + "，将继续排队");
        }
        
        return allocations;
    }
}