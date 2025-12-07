package com.colin.java.matchmake;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

// 交易方向
enum Side {
    BUY, SELL
}

// 订单类型枚举
enum OrderType {
    EFF("Exact-Full-FOK", "要么全成，要么全废，价格必须打平"),
    MLI("Min-Lot-IOC", "最小手数以上能成多少算多少，剩的立即撤"),
    PFG("Partial-Fill-GTC", "允许零头，慢慢排队，直到完成为止"),
    PRM("Pro-Rata-Min-Display", "同价档按展示量比例分，每人都得≥最小手数"),
    AMC("Auction-Match-Cross", "集合竞价，一根价格横线全部打光"),
    OLO("Odd-Lot-Overflow", "碎股先吃，吃不掉扔回 Overflow 队列重试");

    private final String fullName;
    private final String description;

    OrderType(String fullName, String description) {
        this.fullName = fullName;
        this.description = description;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }
}

// 订单类
public class Order {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    long id;
    Side side;
    BigDecimal price; // 金额务必使用BigDecimal，避免double精度丢失
    long quantity;
    long timestamp;
    OrderType orderType;
    long minLot; // 最小手数，默认为1
    int maxRetries; // 最大重试次数，用于OLO策略

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