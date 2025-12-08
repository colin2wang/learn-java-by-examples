package com.colin.java.market.oms.order;

// 订单类型枚举
public enum OrderType {
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
