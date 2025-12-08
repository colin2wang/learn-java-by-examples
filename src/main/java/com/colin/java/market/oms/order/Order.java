package com.colin.java.market.oms.order;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

// 订单类
public class Order {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    long id;
    public Side side;
    public BigDecimal price; // 金额务必使用BigDecimal，避免double精度丢失
    public long quantity;
    long timestamp;
    public OrderType orderType;
    public long minLot; // 最小手数，默认为1
    public int maxRetries; // 最大重试次数，用于OLO策略

    public Order(Side side, double price, long quantity) {
        this(side, price, quantity, OrderType.PFG); // 默认使用Partial-Fill-GTC策略
    }

    public Order(Side side, double price, long quantity, OrderType orderType) {
        this.id = ID_GENERATOR.incrementAndGet();
        this.side = side;
        this.price = BigDecimal.valueOf(price);
        this.quantity = quantity;
        this.timestamp = System.nanoTime(); // 模拟纳秒级时间戳
        this.orderType = orderType;
        this.minLot = 1; // 默认最小手数为1
        this.maxRetries = 3; // 默认最大重试次数为3
    }

    @Override
    public String toString() {
        return String.format("[%s] %s 价格:%.2f 数量:%d (ID:%d, 类型:%s)",
                side, (side == Side.BUY ? "买入" : "卖出"), price, quantity, id, orderType.getFullName());
    }
}