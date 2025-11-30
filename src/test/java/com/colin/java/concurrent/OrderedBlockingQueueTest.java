package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class OrderedBlockingQueueTest {

    // 包装类，实现 Comparable 接口以支持优先级队列排序
    static class Message implements Comparable<Message> {
        int id;      // 序号，用于排序
        String data; // 实际数据

        public Message(int id, String data) {
            this.id = id;
            this.data = data;
        }

        @Override
        public int compareTo(Message o) {
            return Integer.compare(this.id, o.id); // 升序排列
        }
    }

    @Test
    void testMultiProducerMultiConsumerWithOrder() {
        final int TOTAL_COUNT = 20;
        final int PRODUCER_COUNT = 2;
        final int CONSUMER_COUNT = 2;

        // 1. 使用优先级队列，确保无论谁先入队，取出的永远是当前最小序号
        PriorityBlockingQueue<Message> queue = new PriorityBlockingQueue<>();

        // 2. 生产者的共享计数器，保证生成的序号唯一且连续
        AtomicInteger sequenceGenerator = new AtomicInteger(0);

        // 3. 结果集使用数组（或 ConcurrentHashMap），通过索引直接定位，避免 add 的顺序问题
        String[] resultArray = new String[TOTAL_COUNT];

        // 用于等待线程结束
        CountDownLatch producerLatch = new CountDownLatch(PRODUCER_COUNT);
        CountDownLatch consumerLatch = new CountDownLatch(CONSUMER_COUNT);

        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {

            // --- 启动 2 个生产者 ---
            for (int i = 0; i < PRODUCER_COUNT; i++) {
                new Thread(() -> {
                    try {
                        while (true) {
                            // 获取下一个序号
                            int id = sequenceGenerator.getAndIncrement();
                            if (id >= TOTAL_COUNT) break; // 生产完毕

                            // 模拟生产耗时带来的随机性
                            Thread.sleep((long) (Math.random() * 10));

                            // 即使 id=1 的线程比 id=0 的线程先 put，PriorityQueue 也会在内部把 0 排在前面
                            queue.put(new Message(id, "Payload-" + id));
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        producerLatch.countDown();
                    }
                }).start();
            }

            // --- 启动 2 个消费者 ---
            for (int i = 0; i < CONSUMER_COUNT; i++) {
                new Thread(() -> {
                    try {
                        while (true) {
                            // 如果所有生产者结束且队列为空，则退出
                            if (producerLatch.getCount() == 0 && queue.isEmpty()) {
                                break;
                            }

                            // 带有超时时间的 poll，防止在最后时刻死锁
                            Message msg = queue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);

                            if (msg != null) {
                                // 模拟消费耗时带来的随机性
                                Thread.sleep((long) (Math.random() * 10));

                                // 【关键】不要使用 List.add()，而是根据 ID 放入指定位置
                                resultArray[msg.id] = msg.data;
                                System.out.println(Thread.currentThread().getName() + " consumed ID: " + msg.id);
                            }
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        consumerLatch.countDown();
                    }
                }).start();
            }

            producerLatch.await();
            consumerLatch.await();
        });

        // 4. 验证：检查数组中的数据是否按顺序排列
        for (int i = 0; i < TOTAL_COUNT; i++) {
            assertEquals("Payload-" + i, resultArray[i], "索引 " + i + " 的数据应该匹配");
        }
    }
}