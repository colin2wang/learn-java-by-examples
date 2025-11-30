package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class BlockingQueueTest {

    @Test
    void testProducerConsumerWithBlockingQueue() {
        // 1. 准备数据
        final int TOTAL_COUNT = 10;
        // 定义一个容量为 2 的阻塞队列，强制触发阻塞行为（生产快于消费时）
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);

        // 用于收集消费者收到的数据，验证正确性 (使用线程安全的 List)
        List<Integer> receivedData = Collections.synchronizedList(new ArrayList<>());

        // 2. 设置超时保护 (防止死锁或挂起)
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {

            // --- 生产者线程 ---
            Thread producer = new Thread(() -> {
                try {
                    for (int i = 0; i < TOTAL_COUNT; i++) {
                        queue.put(i); // 队列满时会自动阻塞
                        System.out.println("Produced: " + i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // --- 消费者线程 ---
            Thread consumer = new Thread(() -> {
                try {
                    for (int i = 0; i < TOTAL_COUNT; i++) {
                        Integer data = queue.take(); // 队列空时会自动阻塞
                        receivedData.add(data);
                        System.out.println("Consumed: " + data);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // 3. 启动并等待结束
            producer.start();
            consumer.start();

            producer.join();
            consumer.join();
        });

        // 4. 验证数据完整性和顺序
        assertEquals(TOTAL_COUNT, receivedData.size(), "消费的数量应等于生产的数量");
        for (int i = 0; i < TOTAL_COUNT; i++) {
            assertEquals(i, receivedData.get(i), "消费的数据顺序应正确");
        }
    }
}