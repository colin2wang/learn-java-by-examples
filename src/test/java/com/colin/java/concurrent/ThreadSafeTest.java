package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeTest {

    @Test
    public void testOriginal() {
        Map<Integer, Integer> map = new HashMap<>();
        Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(map);

        synchronizedMap.put(1, 1);
        synchronizedMap.put(2, 2);
    }

    // 测试普通HashMap的线程不安全性
    @Test
    public void testHashMapThreadSafety() throws InterruptedException {
        final Map<Integer, Integer> hashMap = new HashMap<>();
        final int threadCount = 10;
        final int operationsPerThread = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        System.out.println("开始测试普通HashMap的线程安全性...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = (threadId * operationsPerThread + j) % 100;
                        hashMap.put(key, j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("普通HashMap测试完成，耗时: " + (endTime - startTime) + "ms");
        System.out.println("普通HashMap最终大小: " + hashMap.size());
        System.out.println("注意：由于线程不安全，结果可能小于预期的10000个操作后的大小，甚至可能抛出异常");
    }

    // 测试Collections.synchronizedMap的线程安全性
    @Test
    public void testSynchronizedMapThreadSafety() throws InterruptedException {
        final Map<Integer, Integer> hashMap = new HashMap<>();
        final Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(hashMap);
        final int threadCount = 10;
        final int operationsPerThread = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        System.out.println("开始测试Collections.synchronizedMap的线程安全性...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        int key = (threadId * operationsPerThread + j) % 100;
                        synchronizedMap.put(key, j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Collections.synchronizedMap测试完成，耗时: " + (endTime - startTime) + "ms");
        System.out.println("Collections.synchronizedMap最终大小: " + synchronizedMap.size());
        System.out.println("由于线程安全，结果应该是预期的大小，不会抛出异常");
    }

    // 演示HashMap在并发环境下可能出现的问题：死循环（JDK 1.7链表头插法导致）
    // 注意：在JDK 1.8+中，HashMap已使用尾插法，很难直接复现死循环，但这里通过模拟展示概念
    @Test
    public void demonstrateHashMapInfiniteLoop() throws InterruptedException {
        System.out.println("===== 演示HashMap并发死循环问题（概念说明） =====");
        System.out.println("注意：在JDK 1.7中，HashMap使用链表头插法实现，在并发扩容时可能形成环形链表导致死循环");
        System.out.println("在JDK 1.8+中，已改用尾插法并加入红黑树优化，直接复现死循环困难");
        System.out.println("以下是模拟场景说明：");
        System.out.println("1. 线程A和线程B同时对HashMap进行扩容");
        System.out.println("2. 线程A读取到链表并开始重新哈希，但被挂起");
        System.out.println("3. 线程B完成扩容并修改了链表结构（头插法导致顺序反转）");
        System.out.println("4. 线程A恢复执行，使用已过时的链表信息继续操作");
        System.out.println("5. 最终形成环形链表，导致后续get操作进入死循环");
        System.out.println("解决方法：使用ConcurrentHashMap或Collections.synchronizedMap");
    }

    // 演示HashMap在并发环境下的数据丢失问题
    @Test
    public void demonstrateHashMapDataLoss() throws InterruptedException {
        System.out.println("===== 演示HashMap并发数据丢失问题 =====");
        final Map<String, Integer> hashMap = new HashMap<>();
        final int threadCount = 20;
        final int iterations = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger putCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        // 使用不同的key避免覆盖
                        String key = "thread-" + threadId + "-" + j;
                        hashMap.put(key, j);
                        putCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        int expectedSize = threadCount * iterations;
        int actualSize = hashMap.size();
        int putOperations = putCount.get();

        System.out.println("执行的put操作次数: " + putOperations);
        System.out.println("期望的Map大小: " + expectedSize);
        System.out.println("实际的Map大小: " + actualSize);
        System.out.println("数据丢失数量: " + (expectedSize - actualSize));
        System.out.println("数据丢失原因：并发put时，两个线程可能同时检测到同一个槽位为空，导致后写入的数据覆盖前写入的数据");
    }

    // 演示HashMap在并发环境下的数据覆盖问题
    @Test
    public void demonstrateHashMapDataOverwrite() throws InterruptedException {
        System.out.println("===== 演示HashMap并发数据覆盖问题 =====");
        final Map<String, String> hashMap = new HashMap<>();
        final int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger finalValueCount = new AtomicInteger(0);

        // 所有线程都对同一个key进行操作，模拟数据覆盖
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                try {
                    // 每个线程多次尝试写入自己的标识
                    for (int j = 0; j < 100; j++) {
                        hashMap.put("shared-key", "value-from-thread-" + threadId + "-attempt-" + j);
                        // 小延迟增加竞争条件概率
                        Thread.sleep(1);
                    }
                    // 最后读取一次最终值
                    String finalValue = hashMap.get("shared-key");
                    System.out.println("线程" + threadId + "读取到的最终值: " + finalValue);
                    if (finalValue != null) {
                        finalValueCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        String finalSharedValue = hashMap.get("shared-key");
        System.out.println("最终存储在Map中的值: " + finalSharedValue);
        System.out.println("数据覆盖问题说明：多个线程并发修改同一个key的值时，最终结果取决于哪个线程最后完成写入");
        System.out.println("所有中间写入的值都被覆盖，造成数据丢失");
    }
}