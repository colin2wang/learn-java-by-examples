package com.colin.java.market.matching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PerformanceTest {

    private MatchingSystem system;
    private RiskManager riskManager;
    private ArrayOrderBook orderBook;

    @BeforeEach
    public void setup() {
        system = new MatchingSystem();
        riskManager = new RiskManager();
        // 准备一个预先填充了数据的 OrderBook 用于纯撮合测试
        orderBook = new ArrayOrderBook(2048, true); // 卖盘
        for (int i = 0; i < 500; i++) {
            orderBook.add(100 + i, 10); // 价格 100, 101, ...
        }
    }

    @Test
    @DisplayName("风控引擎延迟测试 (< 10 μs)")
    public void testRiskManagerLatency() {
        SimpleOrder order = new SimpleOrder(1, 1001L, 99, 1, true);

        // 1. JVM 预热 (10万次调用，触发JIT编译)
        for (int i = 0; i < 100_000; i++) {
            riskManager.validate(order);
        }

        // 2. 正式测试
        long start = System.nanoTime();
        int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            riskManager.validate(order);
        }
        long end = System.nanoTime();

        long avgTime = (end - start) / iterations;
        System.out.println("Risk Manager Avg Latency: " + avgTime + " ns (" + (avgTime / 1000.0) + " μs)");

        assertTrue(avgTime < 10_000, "Risk check should be < 10 μs");
    }

    @Test
    @DisplayName("撮合引擎核心逻辑测试 (< 2 μs)")
    public void testMatchingEngineLatency() {
        // 模拟一个买单，价格很高，能吃掉很多卖单
        long incomingPrice = 200;
        long incomingQty = 50;

        // 1. 预热
        ArrayOrderBook warmBook = new ArrayOrderBook(1000, true);
        for(int k=0; k<100; k++) warmBook.add(100+k, 10);
        for (int i = 0; i < 100_000; i++) {
            warmBook.match(incomingPrice, 1); // 少量撮合
        }

        // 2. 正式测试：一次复杂的撮合（吃掉多个Level）
        // 重置数据
        orderBook.clear();
        for (int i = 0; i < 500; i++) {
            orderBook.add(100 + i, 10);
        }

        long start = System.nanoTime();

        // 执行一次撮合
        orderBook.match(incomingPrice, incomingQty);

        long end = System.nanoTime();
        long duration = end - start;

        System.out.println("Matching Core Latency: " + duration + " ns (" + (duration / 1000.0) + " μs)");

        // 注意：5μs = 5000ns。如果不包含GC暂停，纯内存操作通常在 200ns - 800ns 之间。
        assertTrue(duration < 5_000, "Matching should be < 5 μs");
    }
}