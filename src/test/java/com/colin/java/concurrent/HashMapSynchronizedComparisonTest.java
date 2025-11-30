package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试类，演示HashMap在使用Collections.synchronizedMap包装前后的线程安全差异
 */
public class HashMapSynchronizedComparisonTest {

    private static final Logger LOG = LoggerFactory.getLogger(HashMapSynchronizedComparisonTest.class);
    private static final int THREAD_COUNT = 50;
    private static final int OPERATIONS_PER_THREAD = 1000;
    private static final int TIMEOUT_SECONDS = 60;

    /**
     * 测试普通HashMap在并发环境下的线程不安全性
     * 演示问题：
     * 1. 数据丢失
     * 2. 可能出现的异常
     * 3. 最终大小可能小于预期
     */
    @Test
    public void testHashMapThreadUnsafe() throws InterruptedException {
        LOG.info("===== 测试普通HashMap的线程不安全性 =====");
        final Map<Integer, Integer> hashMap = new HashMap<>();
        final AtomicInteger putOperations = new AtomicInteger(0);
        final AtomicInteger exceptionsCaught = new AtomicInteger(0);
        
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        try {
            // 创建多个线程并发操作HashMap
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executorService.submit(() -> {
                    try {
                        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                            try {
                                // 使用线程ID和操作序号的组合作为键，减少冲突但仍保留一定的并发操作
                                int key = (threadId * 1000 + j) % 100; // 限制key范围以增加冲突概率
                                hashMap.put(key, j);
                                putOperations.incrementAndGet();
                            } catch (Exception e) {
                                LOG.error("线程{}执行put操作时发生异常: {}", threadId, e.getMessage());
                                exceptionsCaught.incrementAndGet();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // 等待所有线程完成
            boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!completed) {
                LOG.warn("测试超时，可能发生了死循环或其他严重问题");
            }
            
            LOG.info("执行的put操作总数: {}", putOperations.get());
            LOG.info("捕获的异常数量: {}", exceptionsCaught.get());
            LOG.info("HashMap最终大小: {}", hashMap.size());
            LOG.info("由于HashMap不是线程安全的，最终大小可能小于预期或出现异常");
            
            // 注意：我们不进行严格的断言，因为线程安全问题可能不会每次都以相同方式表现
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }
    
    /**
     * 测试使用Collections.synchronizedMap包装后的线程安全性
     * 应该不会出现异常，且所有操作都能正确完成
     */
    @Test
    public void testSynchronizedMapThreadSafe() throws InterruptedException {
        LOG.info("===== 测试Collections.synchronizedMap的线程安全性 =====");
        final Map<Integer, Integer> hashMap = new HashMap<>();
        final Map<Integer, Integer> synchronizedMap = Collections.synchronizedMap(hashMap);
        final AtomicInteger putOperations = new AtomicInteger(0);
        final AtomicInteger exceptionsCaught = new AtomicInteger(0);
        
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        try {
            // 创建多个线程并发操作synchronizedMap
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executorService.submit(() -> {
                    try {
                        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                            try {
                                // 使用与上面相同的键生成策略
                                int key = (threadId * 1000 + j) % 100;
                                synchronizedMap.put(key, j);
                                putOperations.incrementAndGet();
                            } catch (Exception e) {
                                LOG.error("线程{}执行put操作时发生异常: {}", threadId, e.getMessage());
                                exceptionsCaught.incrementAndGet();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // 等待所有线程完成
            boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!completed) {
                LOG.warn("测试超时");
            }
            
            LOG.info("执行的put操作总数: {}", putOperations.get());
            LOG.info("捕获的异常数量: {}", exceptionsCaught.get());
            LOG.info("synchronizedMap最终大小: {}", synchronizedMap.size());
            
            // 断言不应有异常抛出
            assertEquals(0, exceptionsCaught.get(), "Collections.synchronizedMap不应在并发操作中抛出异常");
            LOG.info("由于使用了Collections.synchronizedMap，操作是线程安全的，不会抛出异常");
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }
    
    /**
     * 对比测试：同时运行两种Map实现，观察差异
     */
    @Test
    public void compareThreadSafety() throws InterruptedException {
        LOG.info("===== 对比HashMap和synchronizedMap的线程安全性 =====");
        
        // 运行普通HashMap测试
        testHashMapThreadUnsafe();
        
        // 运行synchronizedMap测试
        testSynchronizedMapThreadSafe();
        
        LOG.info("===== 对比测试完成 =====");
        LOG.info("结论：");
        LOG.info("1. 普通HashMap在并发环境下不是线程安全的，可能导致数据丢失、异常或其他问题");
        LOG.info("2. Collections.synchronizedMap通过同步方法保证了基本的线程安全");
        LOG.info("3. 对于更复杂的并发场景，推荐使用ConcurrentHashMap，它提供了更好的并发性能");
    }
}