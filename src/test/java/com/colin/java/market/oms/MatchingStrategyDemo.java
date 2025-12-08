package com.colin.java.market.oms;

import com.colin.java.market.oms.order.Order;
import com.colin.java.market.oms.order.OrderBook;
import com.colin.java.market.oms.order.OrderType;
import com.colin.java.market.oms.order.Side;

/**
 * 撮合策略演示类
 * 展示六种不同撮合策略的使用方法
 */
public class MatchingStrategyDemo {
    
    public static void main(String[] args) {
        System.out.println("========== 撮合策略演示 ==========");
        
        // 创建订单簿
        OrderBook orderBook = new OrderBook();
        
        // 1. 演示 Exact-Full-FOK (EFF) 策略
        System.out.println("\n=== 1. Exact-Full-FOK 策略演示 ===");
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10, OrderType.EFF));
        orderBook.processOrder(new Order(Side.BUY, 100.00, 10, OrderType.EFF)); // 完全匹配
        orderBook.processOrder(new Order(Side.BUY, 100.00, 9, OrderType.EFF));  // 数量不匹配，整单取消
        orderBook.printDepth();
        
        // 清空订单簿并演示 Min-Lot-IOC (MLI) 策略
        orderBook = new OrderBook();
        System.out.println("\n=== 2. Min-Lot-IOC 策略演示 ===");
        orderBook.processOrder(new Order(Side.SELL, 100.00, 5));
        orderBook.processOrder(new Order(Side.SELL, 101.00, 6));
        Order mliOrder = new Order(Side.BUY, 102.00, 8, OrderType.MLI);
        mliOrder.minLot = 3; // 设置最小手数为3
        orderBook.processOrder(mliOrder); // 部分成交，剩余数量小于最小手数自动撤单
        orderBook.printDepth();
        
        // 清空订单簿并演示 Partial-Fill-GTC (PFG) 策略
        orderBook = new OrderBook();
        System.out.println("\n=== 3. Partial-Fill-GTC 策略演示 ===");
        orderBook.processOrder(new Order(Side.SELL, 100.00, 5));
        orderBook.processOrder(new Order(Side.BUY, 101.00, 8, OrderType.PFG)); // 部分成交，剩余挂单
        orderBook.printDepth();
        
        // 演示 Pro-Rata-Min-Display (PRM) 策略
        System.out.println("\n=== 4. Pro-Rata-Min-Display 策略演示 ===");
        orderBook.processOrder(new Order(Side.SELL, 102.00, 10));
        orderBook.processOrder(new Order(Side.SELL, 102.00, 20));
        Order prmOrder = new Order(Side.BUY, 102.00, 15, OrderType.PRM);
        prmOrder.minLot = 3;
        orderBook.processOrder(prmOrder); // 按比例分配
        orderBook.printDepth();
        
        // 清空订单簿并演示 Auction-Match-Cross (AMC) 策略
        orderBook = new OrderBook();
        System.out.println("\n=== 5. Auction-Match-Cross 策略演示 ===");
        orderBook.processOrder(new Order(Side.BUY, 105.00, 5));
        orderBook.processOrder(new Order(Side.BUY, 104.00, 10));
        orderBook.processOrder(new Order(Side.SELL, 103.00, 8));
        orderBook.processOrder(new Order(Side.SELL, 102.00, 7));
        orderBook.processOrder(new Order(Side.BUY, 101.00, 12, OrderType.AMC)); // 集合竞价
        orderBook.printDepth();
        
        // 清空订单簿并演示 Odd-Lot-Overflow (OLO) 策略
        orderBook = new OrderBook();
        System.out.println("\n=== 6. Odd-Lot-Overflow 策略演示 ===");
        orderBook.processOrder(new Order(Side.SELL, 100.00, 2)); // 碎股卖单
        Order oloOrder = new Order(Side.BUY, 100.00, 5, OrderType.OLO);
        oloOrder.maxRetries = 2;
        orderBook.processOrder(oloOrder); // 允许碎股成交，剩余进入溢出队列
        orderBook.printDepth();
        
        System.out.println("\n========== 演示结束 ==========");
    }
}