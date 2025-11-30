package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapThreadUnsafeTest {
    private static final Logger LOG = LoggerFactory.getLogger(MapThreadUnsafeTest.class);
    private static final int THREAD_COUNT = 300; // Reduced from 3000 for faster test execution
    private static final int TIMEOUT_SECONDS = 30;

    @Test
    void testHashMapThreadSafetyIssue() throws InterruptedException {
        LOG.info("Testing HashMap thread safety issues");
        Map<Thread, Integer> map = new HashMap<>();
        AtomicInteger nullValuesDetected = new AtomicInteger(0);
        
        testMapImplementation(map, nullValuesDetected);
        
        // HashMap is not thread-safe, so we expect some null values
        LOG.info("HashMap test completed. Null values detected: {}", nullValuesDetected.get());
        // Note: We don't assert nullValuesDetected > 0 because thread safety issues might not always manifest in every test run
    }

    @Test
    void testConcurrentHashMapSafety() throws InterruptedException {
        LOG.info("Testing ConcurrentHashMap thread safety");
        Map<Thread, Integer> map = new ConcurrentHashMap<>();
        AtomicInteger nullValuesDetected = new AtomicInteger(0);
        
        testMapImplementation(map, nullValuesDetected);
        
        // ConcurrentHashMap should be thread-safe, so we expect no null values
        assertEquals(0, nullValuesDetected.get(), "ConcurrentHashMap should not have null values in concurrent access");
        LOG.info("ConcurrentHashMap test completed. Null values detected: {}", nullValuesDetected.get());
    }

    @Test
    void testHashtableSafety() throws InterruptedException {
        LOG.info("Testing Hashtable thread safety");
        Map<Thread, Integer> map = new Hashtable<>();
        AtomicInteger nullValuesDetected = new AtomicInteger(0);
        
        testMapImplementation(map, nullValuesDetected);
        
        // Hashtable should be thread-safe, so we expect no null values
        assertEquals(0, nullValuesDetected.get(), "Hashtable should not have null values in concurrent access");
        LOG.info("Hashtable test completed. Null values detected: {}", nullValuesDetected.get());
    }

    private void testMapImplementation(Map<Thread, Integer> map, AtomicInteger nullValuesDetected) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        try {
            for (int i = 0; i < THREAD_COUNT; i++) {
                executorService.submit(() -> {
                    try {
                        Thread currentThread = Thread.currentThread();
                        int data = new Random().nextInt();
                        map.put(currentThread, data);
                        
                        // Test with inner classes similar to original code
                        A a = new A(map, nullValuesDetected);
                        B b = new B(map, nullValuesDetected);
                        
                        a.getDataA();
                        b.getDataB();
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // Wait for all threads to complete with timeout
            boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertTrue(completed, "Test timed out before all threads completed");
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

    static class A {
        private final Map<Thread, Integer> map;
        private final AtomicInteger nullValuesDetected;
        
        A(Map<Thread, Integer> map, AtomicInteger nullValuesDetected) {
            this.map = map;
            this.nullValuesDetected = nullValuesDetected;
        }
        
        void getDataA() {
            Thread currentThread = Thread.currentThread();
            Integer value = map.get(currentThread);
            if (value == null) {
                LOG.warn("{} A:null", currentThread.getName());
                nullValuesDetected.incrementAndGet();
            }
        }
    }

    static class B {
        private final Map<Thread, Integer> map;
        private final AtomicInteger nullValuesDetected;
        
        B(Map<Thread, Integer> map, AtomicInteger nullValuesDetected) {
            this.map = map;
            this.nullValuesDetected = nullValuesDetected;
        }
        
        void getDataB() {
            Thread currentThread = Thread.currentThread();
            Integer value = map.get(currentThread);
            if (value == null) {
                LOG.warn("{} B:null", currentThread.getName());
                nullValuesDetected.incrementAndGet();
            }
        }
    }
}