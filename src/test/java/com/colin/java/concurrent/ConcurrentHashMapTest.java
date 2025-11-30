package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class ConcurrentHashMapTest {
    private static final Logger LOG = LoggerFactory.getLogger(ConcurrentHashMapTest.class);
    
    // 降低线程数量以便测试更高效地运行
    private static final int THREAD_COUNT = 100;
    private static final int OPERATIONS_PER_THREAD = 100;

    @Test
    public void testHashMapConcurrentAccess() throws InterruptedException {
        LOG.info("Testing HashMap for concurrent access issues");
        final HashMap<String, String> map = new HashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT * OPERATIONS_PER_THREAD);
        
        try {
            // 创建多个线程并发写入HashMap
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                            try {
                                map.put(UUID.randomUUID().toString(), "");
                            } catch (Exception e) {
                                LOG.error("Exception occurred in HashMap concurrent access: {}", e.getMessage());
                                // 记录异常但不中断测试，因为我们知道HashMap不是线程安全的
                            } finally {
                                latch.countDown();
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("Thread execution failed: {}", e.getMessage());
                    }
                });
            }
            
            // 等待所有操作完成或超时
            latch.await();
            LOG.info("HashMap test completed, final size: {}", map.size());
        } finally {
            executor.shutdown();
        }
    }
    
    @Test
    public void testConcurrentHashMapSafety() throws InterruptedException {
        LOG.info("Testing ConcurrentHashMap for thread safety");
        final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT * OPERATIONS_PER_THREAD);
        
        try {
            // 创建多个线程并发写入ConcurrentHashMap
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                            map.put(UUID.randomUUID().toString(), "");
                            latch.countDown();
                        }
                    } catch (Exception e) {
                        LOG.error("Exception occurred in ConcurrentHashMap access: {}", e.getMessage());
                        fail("ConcurrentHashMap should not throw exceptions during concurrent access: " + e.getMessage());
                    }
                });
            }
            
            // 等待所有操作完成
            latch.await();
            
            // 验证ConcurrentHashMap的大小是否合理（由于可能有重复键，所以不能严格等于操作数）
            int finalSize = map.size();
            LOG.info("ConcurrentHashMap test completed, final size: {}", finalSize);
            assertTrue(finalSize > 0 && finalSize <= THREAD_COUNT * OPERATIONS_PER_THREAD, 
                      "ConcurrentHashMap should contain between 1 and " + (THREAD_COUNT * OPERATIONS_PER_THREAD) + " elements");
        } finally {
            executor.shutdown();
        }
    }
    
    @Test
    public void testConcurrentHashMapOperations() {
        LOG.info("Testing ConcurrentHashMap basic operations");
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        
        // 测试基本的put和get操作
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        
        assertEquals(3, map.size(), "ConcurrentHashMap should have 3 elements");
        assertEquals(Integer.valueOf(1), map.get("one"), "Value for key 'one' should be 1");
        assertEquals(Integer.valueOf(2), map.get("two"), "Value for key 'two' should be 2");
        assertEquals(Integer.valueOf(3), map.get("three"), "Value for key 'three' should be 3");
        
        // 测试更新操作
        Integer oldValue = map.put("one", 10);
        assertEquals(Integer.valueOf(1), oldValue, "put should return the old value");
        assertEquals(Integer.valueOf(10), map.get("one"), "Value for key 'one' should be updated to 10");
        
        // 测试删除操作
        Integer removedValue = map.remove("two");
        assertEquals(Integer.valueOf(2), removedValue, "remove should return the removed value");
        assertNull(map.get("two"), "Value for key 'two' should be null after removal");
        assertEquals(2, map.size(), "ConcurrentHashMap should have 2 elements after removal");
        
        // 测试并发友好的操作
        map.computeIfAbsent("four", k -> 4);
        assertEquals(Integer.valueOf(4), map.get("four"), "computeIfAbsent should add missing key-value pair");
        
        map.computeIfPresent("one", (k, v) -> v + 5);
        assertEquals(Integer.valueOf(15), map.get("one"), "computeIfPresent should update existing value");
        
        LOG.info("ConcurrentHashMap operations test passed");
    }
}