package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class LockConditionTest {

    // --- 手写一个简单的有界缓冲区 (内部类) ---
    static class MyBoundedBuffer<T> {
        private final LinkedList<T> buffer = new LinkedList<>();
        private final int capacity;
        private final Lock lock = new ReentrantLock();
        // 定义两个 Condition：不满(用于生产者)、不空(用于消费者)
        private final Condition notFull = lock.newCondition();
        private final Condition notEmpty = lock.newCondition();

        public MyBoundedBuffer(int capacity) {
            this.capacity = capacity;
        }

        public void put(T data) throws InterruptedException {
            lock.lock();
            try {
                // while循环检查：防止虚假唤醒
                while (buffer.size() >= capacity) {
                    System.out.println("Queue is full, Producer waiting...");
                    notFull.await(); // 满了，等待“不满”的信号
                }
                buffer.add(data);
                // 生产了数据，队列肯定“不空”了，唤醒消费者
                notEmpty.signal();
            } finally {
                lock.unlock();
            }
        }

        public T take() throws InterruptedException {
            lock.lock();
            try {
                while (buffer.isEmpty()) {
                    System.out.println("Queue is empty, Consumer waiting...");
                    notEmpty.await(); // 空了，等待“不空”的信号
                }
                T data = buffer.removeFirst();
                // 消费了数据，队列肯定“不满”了，唤醒生产者
                notFull.signal();
                return data;
            } finally {
                lock.unlock();
            }
        }
    }

    // --- JUnit 测试 ---
    @Test
    void testProducerConsumerWithLockCondition() {
        final int TOTAL_COUNT = 10;
        final int CAPACITY = 2;

        // 实例化我们手写的缓冲区
        MyBoundedBuffer<Integer> buffer = new MyBoundedBuffer<>(CAPACITY);
        List<Integer> results = Collections.synchronizedList(new ArrayList<>());

        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            Thread producer = new Thread(() -> {
                try {
                    for (int i = 0; i < TOTAL_COUNT; i++) {
                        buffer.put(i);
                        System.out.println("Produced: " + i);
                    }
                } catch (InterruptedException e) { e.printStackTrace(); }
            });

            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 0; i < TOTAL_COUNT; i++) {
                        Integer val = buffer.take();
                        results.add(val);
                        System.out.println("Consumed: " + val);
                        // 模拟消费耗时，让 buffer 有机会被填满，触发 wait
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) { e.printStackTrace(); }
            });

            producer.start();
            consumer.start();

            producer.join();
            consumer.join();
        });

        // 验证
        assertEquals(TOTAL_COUNT, results.size());
        for (int i = 0; i < TOTAL_COUNT; i++) {
            assertEquals(i, results.get(i));
        }
    }
}