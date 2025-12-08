package com.colin.java.market.oms.strategy;

import com.colin.java.market.oms.order.Order;
import com.colin.java.market.oms.order.OrderBook;
import com.colin.java.market.oms.order.Side;

import java.math.BigDecimal;
import java.util.*;

/**
 * Auction-Match-Cross 策略实现
 * "集合竞价，一根价格横线全部打光。"
 * 统一成交价 = 最大成交量价位；可 Exact 也可 Partial；未成交部分若带 IOC 则撤，否则转连续竞价。
 */
public class AuctionMatchCrossStrategy extends AbstractMatchingStrategy {
    
    public AuctionMatchCrossStrategy() {
        super("AMC", "集合竞价，一根价格横线全部打光。统一成交价 = 最大成交量价位；可 Exact 也可 Partial；未成交部分若带 IOC 则撤，否则转连续竞价。");
    }
    
    @Override
    public boolean match(Order order, OrderBook orderBook) {
        // 集合竞价需要收集所有买单和卖单
        List<Order> buyOrders = new ArrayList<>(orderBook.buyOrders);
        List<Order> sellOrders = new ArrayList<>(orderBook.sellOrders);
        
        // 添加当前订单到相应队列
        if (order.side == Side.BUY) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
        
        // 按价格排序买单（降序）和卖单（升序）
        buyOrders.sort((o1, o2) -> o2.price.compareTo(o1.price));
        sellOrders.sort((o1, o2) -> o1.price.compareTo(o2.price));
        
        // 找到最大成交量价位
        BigDecimal clearingPrice = findClearingPrice(buyOrders, sellOrders);
        
        if (clearingPrice == null) {
            System.out.println("[AMC策略] 未找到可成交价格");
            return false;
        }
        
        System.out.println("[AMC策略] 集合竞价成交价: " + clearingPrice);
        
        // 执行成交
        boolean hasMatched = executeAuctionMatching(buyOrders, sellOrders, clearingPrice, orderBook);
        
        return hasMatched;
    }
    
    private BigDecimal findClearingPrice(List<Order> buyOrders, List<Order> sellOrders) {
        // 创建价格列表
        Set<BigDecimal> pricePoints = new TreeSet<>();
        for (Order order : buyOrders) {
            pricePoints.add(order.price);
        }
        for (Order order : sellOrders) {
            pricePoints.add(order.price);
        }
        
        BigDecimal bestPrice = null;
        long maxVolume = 0;
        
        // 计算每个价格点的可成交量
        for (BigDecimal price : pricePoints) {
            long buyVolume = calculateCumulativeVolume(buyOrders, price, true);
            long sellVolume = calculateCumulativeVolume(sellOrders, price, false);
            long volume = Math.min(buyVolume, sellVolume);
            
            if (volume > maxVolume) {
                maxVolume = volume;
                bestPrice = price;
            }
        }
        
        return bestPrice;
    }
    
    private long calculateCumulativeVolume(List<Order> orders, BigDecimal price, boolean isBuy) {
        long volume = 0;
        for (Order order : orders) {
            if (isBuy) {
                if (order.price.compareTo(price) >= 0) {
                    volume += order.quantity;
                }
            } else {
                if (order.price.compareTo(price) <= 0) {
                    volume += order.quantity;
                }
            }
        }
        return volume;
    }
    
    private boolean executeAuctionMatching(List<Order> buyOrders, List<Order> sellOrders, 
                                          BigDecimal clearingPrice, OrderBook orderBook) {
        boolean hasMatched = false;
        
        // 创建临时队列用于成交
        PriorityQueue<Order> tempBuyQueue = new PriorityQueue<>(buyOrders);
        PriorityQueue<Order> tempSellQueue = new PriorityQueue<>(sellOrders);
        
        // 执行成交
        Order buyOrder = tempBuyQueue.poll();
        Order sellOrder = tempSellQueue.poll();
        
        while (buyOrder != null && sellOrder != null) {
            // 检查是否在成交价范围内
            if (buyOrder.price.compareTo(clearingPrice) >= 0 && sellOrder.price.compareTo(clearingPrice) <= 0) {
                long tradeQuantity = Math.min(buyOrder.quantity, sellOrder.quantity);
                
                System.out.println("[AMC策略] 拍卖成交: " + buyOrder + " 与 " + sellOrder + ", 成交价格: " + clearingPrice + ", 成交数量: " + tradeQuantity);
                
                // 减少订单数量
                buyOrder.quantity -= tradeQuantity;
                sellOrder.quantity -= tradeQuantity;
                hasMatched = true;
                
                // 从原订单簿中移除完全成交的订单
                if (buyOrder.quantity == 0) {
                    orderBook.buyOrders.remove(buyOrder);
                    buyOrder = tempBuyQueue.poll();
                } else {
                    // 这里简化处理，实际应该处理剩余订单
                }
                
                if (sellOrder.quantity == 0) {
                    orderBook.sellOrders.remove(sellOrder);
                    sellOrder = tempSellQueue.poll();
                } else {
                    // 这里简化处理，实际应该处理剩余订单
                }
            } else {
                break;
            }
        }
        
        return hasMatched;
    }
}