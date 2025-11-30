package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class DeadlockDemoTest {

    private final Object resourceA = new Object();
    private final Object resourceB = new Object();

    @Test
    void testDeadlock() {
        // 使用 assertTimeoutPreemptively 设置超时时间 (例如 2 秒)
        // 如果代码逻辑发生死锁，无法在 2 秒内完成，测试将失败并抛出超时异常
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {

            Thread thread1 = new Thread(() -> {
                synchronized (resourceA) {
                    System.out.println("Thread 1: holding A...");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}

                    synchronized (resourceB) {
                        System.out.println("Thread 1: holding A & B");
                    }
                }
            });

            Thread thread2 = new Thread(() -> {
                synchronized (resourceB) {
                    System.out.println("Thread 2: holding B...");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}

                    synchronized (resourceA) {
                        System.out.println("Thread 2: holding B & A");
                    }
                }
            });

            thread1.start();
            thread2.start();

            // 必须使用 join() 等待线程结束
            // 否则主线程会直接跑完，导致测试"误判"为通过，而死锁线程还在后台挂着
            thread1.join();
            thread2.join();
        }, "测试失败：发生了死锁或执行时间超过了预期");
    }
}