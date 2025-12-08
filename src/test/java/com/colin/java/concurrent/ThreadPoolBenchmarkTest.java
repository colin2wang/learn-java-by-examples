package com.colin.java.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
@DisplayName("线程池性能对比")
class ThreadPoolBenchmarkTest {

    private static final int TASK_COUNT  = 10_000;
    private static final int SLEEP_NANO  = 1_000;   // 1 µs 模拟 CPU 小耗时

    /** 空壳任务：睡 1 µs 后计数 */
    private static final Runnable TASK = () -> {
        try {
            TimeUnit.NANOSECONDS.sleep(SLEEP_NANO);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    };

    @Test
    @DisplayName("无线程池：单线程顺序执行")
    void sequential() {
        /*
         * sequential方法不需要CountDownLatch的原因：
         * 1. 这是单线程顺序执行模式，任务是一个接一个同步执行的
         * 2. forEach循环会阻塞当前线程，直到所有任务都执行完毕
         * 3. 每个TASK.run()调用都是同步的，只有当前任务完成后，下一个任务才会开始
         * 4. 不需要额外的同步机制，因为方法本身就会等待所有任务执行完毕
         */
        long start = System.nanoTime();
        IntStream.range(0, TASK_COUNT).forEach(i -> TASK.run()); // 同步执行，逐个完成
        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        log.info("sequential 耗时 = {} ms", ms);
    }

    @Test
    @DisplayName("固定线程池：CPU 核数")
    void withThreadPool() throws InterruptedException {
        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        /*
         * withThreadPool方法需要CountDownLatch的原因：
         * 1. 线程池中的任务是异步执行的，pool.execute()调用会立即返回，不会等待任务完成
         * 2. CountDownLatch用于同步主线程和工作线程，确保所有任务完成后再计算总耗时
         * 3. 每个任务执行完毕后调用countDown()减少计数器
         * 4. 主线程通过await()等待计数器归0，表示所有任务已完成
         */
        CountDownLatch latch = new CountDownLatch(TASK_COUNT);

        long start = System.nanoTime();
        for (int i = 0; i < TASK_COUNT; i++) {
            pool.execute(() -> {
                TASK.run();
                latch.countDown(); // 任务完成后递减计数
            });
        }
        latch.await();          // 阻塞等待所有任务完成
        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        pool.shutdown();
        log.info("threadPool({}) 耗时 = {} ms", nThreads, ms);
    }
}