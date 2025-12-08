package com.colin.java.market.matching;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchingSystem {
    // 模拟水平分片：Map <ProductID, Engine>
    // 实际部署中，通常是一个 ProductID 对应一个独立的线程（Thread Affinity）
    private final Map<Long, EngineShard> shards = new ConcurrentHashMap<>();
    private final RiskManager riskManager = new RiskManager();

    public MatchingSystem() {
        // 初始化 ID 为 1001 的产品分片
        shards.put(1001L, new EngineShard());
    }

    // 单个分片引擎，包含买卖盘
    public static class EngineShard {
        ArrayOrderBook buyBook = new ArrayOrderBook(1024, false); // 买盘：高价优先
        ArrayOrderBook sellBook = new ArrayOrderBook(1024, true); // 卖盘：低价优先

        public void process(SimpleOrder order) {
            if (order.isBuy) {
                // 买单进来，去匹配卖盘
                long traded = sellBook.match(order.price, order.quantity);
                long remain = order.quantity - traded;
                if (remain > 0) {
                    buyBook.add(order.price, remain);
                }
            } else {
                // 卖单进来，去匹配买盘
                long traded = buyBook.match(order.price, order.quantity);
                long remain = order.quantity - traded;
                if (remain > 0) {
                    sellBook.add(order.price, remain);
                }
            }
        }
    }

    public void handleOrder(SimpleOrder order) {
        // 1. 预风控
        if (!riskManager.validate(order)) {
            // throw new RuntimeException("Risk Check Failed");
            return;
        }

        // 2. 路由到指定分片 (无锁，分片间无共享)
        EngineShard shard = shards.get(order.productId);
        if (shard != null) {
            // 在真实系统中，这里会将订单放入 RingBuffer (Disruptor)，由消费者线程处理
            // 这里为了测试核心算法速度，直接调用
            shard.process(order);
        }
    }
}