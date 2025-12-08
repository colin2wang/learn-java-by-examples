package com.colin.java.market.oms;

import com.colin.java.market.oms.order.Order;
import com.colin.java.market.oms.order.OrderBook;
import com.colin.java.market.oms.order.Side;

public class StockMatchingDemo {
    public static void main(String[] args) {
        OrderBook orderBook = new OrderBook();

        // 场景 1: 挂入一些卖单 (Maker)
        // 卖单：100元 10股，102元 5股
        orderBook.processOrder(new Order(Side.SELL, 100.00, 10));
        orderBook.processOrder(new Order(Side.SELL, 102.00, 5));

        orderBook.printDepth();

        // 场景 2: 进来一个买单 (Taker)，价格足以吃掉第一个卖单
        // 买单：101元 5股 -> 应该成交 100元的那笔（价格优先，以挂单价成交）
        orderBook.processOrder(new Order(Side.BUY, 101.00, 5));

        orderBook.printDepth();

        // 场景 3: 进来一个大额买单，扫单
        // 买单：103元 20股 -> 应该吃掉剩余的卖单，剩下的挂在买单队列
        // 剩余卖单：100元(剩5股), 102元(5股)
        orderBook.processOrder(new Order(Side.BUY, 103.00, 20));

        orderBook.printDepth();
    }
}