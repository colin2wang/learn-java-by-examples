package com.colin.java.finance;

public class Order {
    public long orderId;
    public long productId;
    public long price;
    public long quantity;
    public boolean isBuy; // true = Buy, false = Sell
    public long timestamp;

    public Order(long orderId, long productId, long price, long quantity, boolean isBuy) {
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.isBuy = isBuy;
        this.timestamp = System.nanoTime();
    }
}