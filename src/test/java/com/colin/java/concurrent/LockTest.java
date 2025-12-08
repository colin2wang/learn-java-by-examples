package com.colin.java.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class LockTest {
    @Test
    void testBasicLockUnlock() {
        log.info("Testing basic lock and unlock functionality");
        Lock lock = new ReentrantLock();
        
        // Test that lock is available initially
        boolean locked = lock.tryLock();
        assertTrue(locked, "Should be able to acquire lock initially");
        
        try {
            log.info("Lock acquired successfully");
            // Verify lock is held
            boolean canReacquire = lock.tryLock();
            assertTrue(canReacquire, "ReentrantLock should allow reentrant acquisition");
            
            try {
                log.info("Reentrant lock acquired successfully");
            } finally {
                lock.unlock();
                log.info("Reentrant lock released");
            }
        } finally {
            lock.unlock();
            log.info("Original lock released");
        }
        
        // Verify lock is now available again
        locked = lock.tryLock();
        assertTrue(locked, "Lock should be available after unlock");
        lock.unlock();
    }

    @Test
    void testLockWithSharedResource() {
        log.info("Testing lock with shared resource access");
        final Lock lock = new ReentrantLock();
        final AtomicInteger counter = new AtomicInteger(0);
        final int iterations = 1000;
        
        // Simulate concurrent access to shared counter
        for (int i = 0; i < iterations; i++) {
            lock.lock();
            try {
                // Critical section - safely update shared resource
                int current = counter.get();
                counter.set(current + 1);
            } finally {
                lock.unlock();
            }
        }
        
        // Verify counter value is correct
        assertEquals(iterations, counter.get(), "Counter should be incremented exactly " + iterations + " times");
        log.info("Counter value: {}", counter.get());
    }

    @Test
    void testConcurrentLocking() throws InterruptedException {
        log.info("Testing concurrent locking behavior");
        final Lock lock = new ReentrantLock();
        final AtomicInteger sharedCounter = new AtomicInteger(0);
        final int numThreads = 5;
        final int operationsPerThread = 100;
        
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        
        try {
            // Submit multiple tasks that compete for the lock
            for (int i = 0; i < numThreads; i++) {
                final int threadId = i;
                executorService.submit(() -> {
                    log.info("Thread {} started", threadId);
                    for (int j = 0; j < operationsPerThread; j++) {
                        lock.lock();
                        try {
                            // Simulate work with shared resource
                            sharedCounter.incrementAndGet();
                            log.debug("Thread {} incremented counter to {}", threadId, sharedCounter.get());
                            // Small sleep to make thread contention more likely
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                    log.info("Thread {} completed", threadId);
                });
            }
        } finally {
            executorService.shutdown();
            boolean terminated = executorService.awaitTermination(10, TimeUnit.SECONDS);
            assertTrue(terminated, "Executor service should terminate within timeout");
        }
        
        // Verify all increments were applied correctly
        int expectedTotal = numThreads * operationsPerThread;
        assertEquals(expectedTotal, sharedCounter.get(), 
                "Counter should be incremented exactly " + expectedTotal + " times by all threads");
        log.info("Final counter value after concurrent operations: {}", sharedCounter.get());
    }
}