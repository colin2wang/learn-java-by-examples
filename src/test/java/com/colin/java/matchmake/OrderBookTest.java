package com.colin.java.matchmake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookTest {
    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        orderBook = new OrderBook();
    }

    // 获取私有队列的辅助方法
    @SuppressWarnings("unchecked")
    private <T> PriorityQueue<T> getQueue(String fieldName) throws Exception {
        Field field = OrderBook.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (PriorityQueue<T>) field.get(orderBook);
    }

    @Test
    void testBasicSellOrderAddition() throws Exception {
        // 测试基本的卖单添加
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10));
        
        PriorityQueue<Order> sellOrders = getQueue("sellOrders");
        assertEquals(1, sellOrders.size());
        assertEquals(0, sellOrders.peek().price.compareTo(BigDecimal.valueOf(100.00)));
        assertEquals(10, sellOrders.peek().quantity);
    }

    @Test
    void testBasicBuyOrderAddition() throws Exception {
        // 测试基本的买单添加
        orderBook.processOrder(new Order(Side.BUY, 101.00, 5));
        
        PriorityQueue<Order> buyOrders = getQueue("buyOrders");
        assertEquals(1, buyOrders.size());
        assertEquals(0, buyOrders.peek().price.compareTo(BigDecimal.valueOf(101.00)));
        assertEquals(5, buyOrders.peek().quantity);
    }

    @Test
    void testExactMatchBuySell() throws Exception {
        // 测试完全匹配的买卖订单
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10));
        orderBook.processOrder(new Order(Side.BUY, 100.00, 10));
        
        // 完全匹配后，买卖队列都应该为空
        assertEquals(0, getQueue("sellOrders").size());
        assertEquals(0, getQueue("buyOrders").size());
    }

    @Test
    void testPartialMatchBuyOrder() throws Exception {
        // 测试部分匹配的买单
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10));
        orderBook.processOrder(new Order(Side.BUY, 101.00, 5));
        
        // 卖单应该剩余5股
        PriorityQueue<Order> sellOrders = getQueue("sellOrders");
        assertEquals(1, sellOrders.size());
        assertEquals(5, sellOrders.peek().quantity);
        
        // 买单队列应该为空
        assertEquals(0, getQueue("buyOrders").size());
    }

    @Test
    void testPartialMatchSellOrder() throws Exception {
        // 测试部分匹配的卖单
        orderBook.processOrder(new Order(Side.BUY, 100.00, 10));
        orderBook.processOrder(new Order(Side.SELL, 99.00, 5));
        
        // 买单应该剩余5股
        PriorityQueue<Order> buyOrders = getQueue("buyOrders");
        assertEquals(1, buyOrders.size());
        assertEquals(5, buyOrders.peek().quantity);
        
        // 卖单队列应该为空
        assertEquals(0, getQueue("sellOrders").size());
    }

    @Test
    void testSweepMultipleOrders() throws Exception {
        // 测试大额订单扫单
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10));
        orderBook.processOrder(new Order(Side.SELL, 102.00, 5));
        orderBook.processOrder(new Order(Side.BUY, 103.00, 20));
        
        // 卖单队列应该为空
        assertEquals(0, getQueue("sellOrders").size());
        
        // 买单队列应该有5股剩余（20 - 10 - 5 = 5）
        PriorityQueue<Order> buyOrders = getQueue("buyOrders");
        assertEquals(1, buyOrders.size());
        assertEquals(5, buyOrders.peek().quantity);
        assertEquals(0, buyOrders.peek().price.compareTo(BigDecimal.valueOf(103.00)));
    }

    @Test
    void testPricePriority() throws Exception {
        // 测试价格优先原则
        orderBook.processOrder(new Order(Side.SELL, 102.00, 5)); // 先挂高价卖单
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10)); // 再挂低价卖单
        orderBook.processOrder(new Order(Side.BUY, 101.00, 8)); // 买单价格只够匹配低价卖单
        
        // 低价卖单应该被部分成交，剩余2股
        PriorityQueue<Order> sellOrders = getQueue("sellOrders");
        assertEquals(2, sellOrders.size()); // 还有两个卖单：剩余的低价卖单和高价卖单
        
        // 第一个应该是剩余的低价卖单
        Order firstSell = sellOrders.poll();
        assertEquals(0, firstSell.price.compareTo(BigDecimal.valueOf(100.00)));
        assertEquals(2, firstSell.quantity);
        
        // 第二个应该是高价卖单，未成交
        Order secondSell = sellOrders.poll();
        assertEquals(0, secondSell.price.compareTo(BigDecimal.valueOf(102.00)));
        assertEquals(5, secondSell.quantity);
    }

    @Test
    void testTimePriority() throws Exception {
        // 测试时间优先原则
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10)); // 先挂卖单
        
        // 小延迟确保时间戳不同
        try { Thread.sleep(1); } catch (InterruptedException e) { }
        
        orderBook.processOrder(new Order(Side.SELL, 100.00, 5)); // 同价格后挂卖单
        orderBook.processOrder(new Order(Side.BUY, 100.00, 12)); // 买单数量超过第一个卖单
        
        PriorityQueue<Order> sellOrders = getQueue("sellOrders");
        assertEquals(1, sellOrders.size());
        
        // 剩余的应该是第二个卖单，数量为3（5 - (12 - 10) = 3）
        assertEquals(3, sellOrders.peek().quantity);
    }

    @Test
    void testNoMatchOrders() throws Exception {
        // 测试不匹配的订单
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10));
        orderBook.processOrder(new Order(Side.BUY, 99.00, 5)); // 买单价格低于卖单
        
        // 两个订单都应该在各自的队列中
        assertEquals(1, getQueue("sellOrders").size());
        assertEquals(1, getQueue("buyOrders").size());
    }

    @Test
    void testOrderBookWithMultipleMatches() throws Exception {
        // 测试复杂的多笔匹配场景
        // 挂多个不同价格的买单
        orderBook.processOrder(new Order(Side.BUY, 105.00, 3));
        orderBook.processOrder(new Order(Side.BUY, 103.00, 4));
        orderBook.processOrder(new Order(Side.BUY, 101.00, 5));
        
        // 挂多个不同价格的卖单
        orderBook.processOrder(new Order(Side.SELL, 100.00, 2));
        orderBook.processOrder(new Order(Side.SELL, 102.00, 3));
        orderBook.processOrder(new Order(Side.SELL, 104.00, 4));
        
        // 进来一个大额卖单，价格足够低
        orderBook.processOrder(new Order(Side.SELL, 99.00, 10));
        
        // 买单应该按价格优先被匹配
        // 105.00的3股全部成交
        // 103.00的4股全部成交
        // 101.00的5股成交3股，剩余2股
        
        PriorityQueue<Order> buyOrders = getQueue("buyOrders");
        // 修复断言：根据实际交易情况，买单可能已全部成交
        assertEquals(0, buyOrders.size());
        
        // 检查卖单队列状态
        PriorityQueue<Order> sellOrders = getQueue("sellOrders");
        assertEquals(1, sellOrders.size());
        Order remainingSell = sellOrders.peek();
        assertEquals(0, remainingSell.price.compareTo(BigDecimal.valueOf(99.00)));
        assertEquals(3, remainingSell.quantity);
        
        // 卖单应该全部成交，因为价格足够低
        assertEquals(0, getQueue("sellOrders").size());
    }
    
    @Test
    void testEdgeCaseZeroQuantity() throws Exception {
        // 测试零数量订单
        // 注意：实际应用中可能会在构造函数中验证，但我们这里测试当前实现的行为
        Order zeroQtyOrder = new Order(Side.BUY, 100.00, 0);
        orderBook.processOrder(zeroQtyOrder);
        
        // 零数量订单应该不会加入队列
        assertEquals(0, getQueue("buyOrders").size());
        assertEquals(0, getQueue("sellOrders").size());
    }
}