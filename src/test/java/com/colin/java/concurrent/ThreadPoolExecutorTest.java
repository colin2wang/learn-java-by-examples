package com.colin.java.concurrent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ThreadPoolExecutor 7 参数验证")
public class ThreadPoolExecutorTest {

    static class ExecutorFactory {

        private final int corePoolSize;
        private final int maximumPoolSize;
        private final long keepAliveTime;
        private final TimeUnit unit;
        private final BlockingQueue<Runnable> workQueue;
        private final ThreadFactory threadFactory;
        private final RejectedExecutionHandler handler;

        public ExecutorFactory(int corePoolSize,
                               int maximumPoolSize,
                               long keepAliveTime,
                               TimeUnit unit,
                               BlockingQueue<Runnable> workQueue,
                               ThreadFactory threadFactory,
                               RejectedExecutionHandler handler) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
            this.unit = unit;
            this.workQueue = workQueue;
            this.threadFactory = threadFactory;
            this.handler = handler;
        }

        public ThreadPoolExecutor build() {
            return new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    unit,
                    workQueue,
                    threadFactory,
                    handler);
        }
    }


    /* ---------- 1. 核心线程数是否生效 ---------- */
    @Test
    @DisplayName("corePoolSize 条线程会立即创建")
    void testCorePoolSize() throws InterruptedException {
        int core = 3;
        ThreadPoolExecutor pool = new ExecutorFactory(
                core, 10, 60, SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()).build();

        CountDownLatch latch = new CountDownLatch(core);
        for (int i = 0; i < core; i++) {
            pool.execute(latch::countDown);
        }
        assertTrue(latch.await(1, SECONDS));
        assertEquals(core, pool.getPoolSize());  // 证明已创建 core 条线程
        pool.shutdown();
    }

    /* ---------- 2. 最大线程数是否生效 ---------- */
    @Test
    @DisplayName("队列满后线程数能涨到 maximumPoolSize")
    void testMaximumPoolSize() throws InterruptedException {
        int core = 2, max = 5;
        // 容量为 1 的队列，很容易满
        SynchronousQueue<Runnable> queue = new SynchronousQueue<>();
        ThreadPoolExecutor pool = new ExecutorFactory(
                core, max, 60, SECONDS,
                queue,
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()).build();

        CountDownLatch latch = new CountDownLatch(max);
        // 提交 max 个任务，SynchronousQueue 会让后面任务必须新建线程
        for (int i = 0; i < max; i++) {
            pool.execute(() -> {
                try {
                    SECONDS.sleep(1);
                } catch (InterruptedException ignored) {
                }
                latch.countDown();
            });
        }
        assertTrue(latch.await(2, SECONDS));
        assertEquals(max, pool.getPoolSize());  // 已涨到 max
        pool.shutdown();
    }

    /* ---------- 3. keepAliveTime 是否生效 ---------- */
    @Test
    @DisplayName("空闲线程超过 keepAliveTime 后被回收")
    void testKeepAliveTime() throws InterruptedException {
        int core = 1, max = 3;
        ThreadPoolExecutor pool = new ExecutorFactory(
                core, max, 200, MILLISECONDS,
                new SynchronousQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()).build();

        CountDownLatch latch = new CountDownLatch(2);
        // Submit two tasks to create core and one temporary thread
        pool.execute(() -> latch.countDown());
        pool.execute(() -> {
            try {
                // Make this task take a bit longer
                MILLISECONDS.sleep(50);
            } catch (InterruptedException ignored) {
            }
            latch.countDown();
        });
        assertTrue(latch.await(1, SECONDS));

        // Current pool size should be 2 (1 core + 1 temporary)
        assertEquals(2, pool.getPoolSize());
        // 等待 keepAliveTime 过期
        MILLISECONDS.sleep(400);
        assertEquals(core, pool.getPoolSize());  // 临时线程被回收
        pool.shutdown();
    }

    /* ---------- 4. 自定义 ThreadFactory 是否生效 ---------- */
    @Test
    @DisplayName("threadFactory 被使用，线程名符合预期")
    void testThreadFactory() throws InterruptedException {
        AtomicInteger seq = new AtomicInteger(0);
        ThreadFactory factory = r -> new Thread(r, "worker-" + seq.incrementAndGet());

        ThreadPoolExecutor pool = new ExecutorFactory(
                1, 1, 60, SECONDS,
                new LinkedBlockingQueue<>(),
                factory,
                new ThreadPoolExecutor.AbortPolicy()).build();

        CountDownLatch latch = new CountDownLatch(1);
        pool.execute(() -> {
            assertTrue(Thread.currentThread().getName().startsWith("worker-"));
            latch.countDown();
        });
        assertTrue(latch.await(1, SECONDS));
        pool.shutdown();
    }

    /* ---------- 5. 拒绝策略是否生效 ---------- */
    @Test
    @DisplayName("队列+线程全满时 handler 触发")
    void testRejectedExecutionHandler(@TempDir Path tempDir) throws IOException, InterruptedException {
        File rejectLog = tempDir.resolve("reject.log").toFile();
        RejectedExecutionHandler handler = (r, executor) -> {
            try (FileWriter w = new FileWriter(rejectLog, true)) {
                w.write("rejected\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        int core = 1, max = 2;
        ThreadPoolExecutor pool = new ExecutorFactory(
                core, max, 60, SECONDS,
                new ArrayBlockingQueue<>(1),  // 队列容量=1
                Executors.defaultThreadFactory(),
                handler).build();

        CountDownLatch block = new CountDownLatch(1);  // 用来阻塞线程
        // 先占满 1 核心 + 1 临时 + 1 队列 = 3 个任务
        pool.execute(() -> {
            try {
                block.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        pool.execute(() -> {
            try {
                block.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        pool.execute(() -> {});  // 进入队列
        // 第 4 个任务必然触发拒绝
        pool.execute(() -> {});

        block.countDown();  // 放行
        pool.shutdown();
        assertTrue(pool.awaitTermination(2, SECONDS));

        // Verify rejection log
        assertTrue(rejectLog.exists());
        assertEquals("rejected", Files.readString(rejectLog.toPath()).trim());
    }
}