package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class AlternatePrintSemaphoreTest {

    @Test
    void testABCPrintWithSemaphore() {
        // 目标：打印 1 到 9
        final int MAX_PRINT = 9;

        // 结果容器
        List<Integer> result = Collections.synchronizedList(new ArrayList<>());

        // 信号量链：A(1) -> B(0) -> C(0)
        Semaphore semA = new Semaphore(1);
        Semaphore semB = new Semaphore(0);
        Semaphore semC = new Semaphore(0);

        // 全局计数器，用于判断是否结束
        AtomicInteger counter = new AtomicInteger(1);

        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {

            // --- 线程 A 的逻辑 ---
            Thread tA = new Thread(() -> {
                while (true) {
                    try {
                        semA.acquire(); // 等待 A 的许可
                        if (counter.get() > MAX_PRINT) {
                            // 必须释放下一个，否则下一个线程可能永远死锁在 acquire
                            semB.release();
                            break;
                        }
                        result.add(counter.getAndIncrement()); // 打印 1, 4, 7...
                        semB.release(); // 把接力棒交给 B
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            // --- 线程 B 的逻辑 ---
            Thread tB = new Thread(() -> {
                while (true) {
                    try {
                        semB.acquire();
                        if (counter.get() > MAX_PRINT) {
                            semC.release();
                            break;
                        }
                        result.add(counter.getAndIncrement()); // 打印 2, 5, 8...
                        semC.release(); // 把接力棒交给 C
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            // --- 线程 C 的逻辑 ---
            Thread tC = new Thread(() -> {
                while (true) {
                    try {
                        semC.acquire();
                        if (counter.get() > MAX_PRINT) {
                            semA.release();
                            break;
                        }
                        result.add(counter.getAndIncrement()); // 打印 3, 6, 9...
                        semA.release(); // 把接力棒交给 A (回到起点)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });

            tA.start(); tB.start(); tC.start();
            tA.join(); tB.join(); tC.join();
        });

        // 验证结果
        assertEquals(9, result.size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), result);
    }
}