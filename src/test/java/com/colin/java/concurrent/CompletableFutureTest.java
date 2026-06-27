package com.colin.java.concurrent;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompletableFutureTest {
    private static ExecutorService pool;

    @BeforeAll
    static void beforeAll() {
        pool = Executors.newFixedThreadPool(2);
    }

    @AfterAll
    static void afterAll() {
        pool.shutdown();
    }

    /**
     * Test asynchronous computation using traditional Callable + Future pattern.
     * Principle: submits a Callable<Integer> to a thread pool, blocks on future.get()
     * with a 1-second timeout, and verifies the sum 1..100 equals 5050.
     */
    @Test
    @DisplayName("Callable + Future 方式")
    void callableFutureWay() throws Exception {
        Callable<Integer> task = new SumCallable(100);

        Future<Integer> future = pool.submit(task);

        // 阻塞拿结果
        Integer result = future.get(1, TimeUnit.SECONDS);

        assertEquals(5050, result);
    }

    /**
     * Test asynchronous computation using CompletableFuture (modern Java 8+ approach).
     * Principle: wraps the same logic in a Supplier, runs it via supplyAsync() on the pool,
     * and chains get() with timeout; demonstrates the functional-style alternative to Future.
     */
    @Test
    @DisplayName("CompletableFuture 方式")
    void completableFutureWay() throws Exception {
        Supplier<Integer> supplier = () -> new SumCallable(100).call();

        CompletableFuture<Integer> cf =
                CompletableFuture.supplyAsync(supplier, pool);

        // 链式处理 + 超时
        Integer result = cf.get(1, TimeUnit.SECONDS);

        assertEquals(5050, result);
    }

    /* ---------- 公共业务逻辑 ---------- */
    private static class SumCallable implements Callable<Integer> {
        private final int n;
        SumCallable(int n) { this.n = n; }

        @Override
        public Integer call() {
            // 模拟耗时
            return IntStream.rangeClosed(1, n)
                    .peek(i -> sleep(10)) // 10 ms * 100 = 1 s
                    .sum();
        }
    }

    private static void sleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
