package com.colin.java.concurrent;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class CyclicBarrierClassicTest {

    private static final int RUNNERS = 10;

    /**
     * Test that 10 threads wait at a CyclicBarrier and start racing together.
     * Principle: each thread has a random preparation time, then calls barrier.await();
     * all threads are released simultaneously when the barrier party count is reached.
     * Asserts the max blocking duration is within a reasonable bound.
     */
    @Test
    @DisplayName("10 线程必须全部就绪才同时开跑")
    void raceStartTogether() throws Exception {
        // 1. 准备 Barrier：party=RUNNERS，当栅栏打开时打印一句
        CyclicBarrier barrier = new CyclicBarrier(RUNNERS,
                () -> System.out.println(">>> 全部就绪，裁判鸣枪 <<<"));

        ExecutorService pool = Executors.newFixedThreadPool(RUNNERS);
        AtomicLong maxDelay = new AtomicLong(0);

        // 2. 提交 10 个任务
        for (int i = 0; i < RUNNERS; i++) {
            final int number = i + 1;
            pool.submit(() -> {
                try {
                    // 阶段 1：随机准备时间
                    long prepare = ThreadLocalRandom.current().nextInt(500);
                    Thread.sleep(prepare);

                    long readyTime = System.nanoTime();
                    System.out.printf("运动员 %02d 准备完毕，等待发枪%n", number);

                    // 阶段 2：栅栏处阻塞
                    barrier.await();

                    // 阶段 3：继续执行，计算被拦了多久
                    long continueTime = System.nanoTime();
                    long blocked = TimeUnit.NANOSECONDS.toMillis(continueTime - readyTime);
                    maxDelay.updateAndGet(v -> Math.max(v, blocked));
                    System.out.printf("运动员 %02d 起跑！栅栏阻塞了 %d ms%n", number, blocked);
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 3. 等所有人跑完
        pool.shutdown();
        Assertions.assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS),
                "线程池未在 5s 内终止");

        // 4. 断言：最大阻塞时间应该在合理范围内
        // 注意：在多线程环境中，线程唤醒时间受系统负载影响较大
        System.out.println("最大阻塞时间 = " + maxDelay.get() + " ms");
        Assertions.assertTrue(maxDelay.get() < 500,
                "Barrier 唤醒耗时过大，可能系统繁忙");
    }

    /**
     * Test CyclicBarrier reusability across multiple rounds.
     * Principle: the same barrier is used for 2 consecutive rounds of 3 threads each;
     * after all parties arrive, the barrier automatically resets, allowing reuse.
     */
    @Test
    void reusable() throws Exception {
        CyclicBarrier cb = new CyclicBarrier(3, () -> System.out.println("--- 一轮结束 ---"));
        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (int round = 1; round <= 2; round++) {
            System.out.println("===== 第 " + round + " 轮 =====");
            CountDownLatch latch = new CountDownLatch(3);
            for (int i = 0; i < 3; i++) {
                pool.submit(() -> {
                    try {
                        Thread.sleep(100); // 模拟工作
                        cb.await();
                        latch.countDown();
                    } catch (Exception ignore) {}
                });
            }
            latch.await(); // 等本轮全部通过
        }
        pool.shutdown();
    }
}