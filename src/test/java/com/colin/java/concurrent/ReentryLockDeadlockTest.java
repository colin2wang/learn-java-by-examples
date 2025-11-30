package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.time.Duration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentryLockDeadlockTest {

    @Test
    void testDeadlockWithTimeout() {
        // 设定预期：如果代码在 5 秒内没有执行完，则抛出异常，测试失败
        // 这证明了内部发生了死锁导致无法结束
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {

            // --- 这里是死锁的具体实现 ---
            Lock lock1 = new ReentrantLock();
            Lock lock2 = new ReentrantLock();

            Thread t1 = new Thread(() -> {
                try {
                    lock1.lock();
                    System.out.println("线程1 获得 lock1");
                    Thread.sleep(100); // 确保线程2能获得 lock2

                    System.out.println("线程1 等待 lock2...");
                    lock2.lock(); // 死锁点
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 这里的 unlock 可能永远无法执行
                    lock2.unlock();
                    lock1.unlock();
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    lock2.lock();
                    System.out.println("线程2 获得 lock2");
                    Thread.sleep(100); // 确保线程1能获得 lock1

                    System.out.println("线程2 等待 lock1...");
                    lock1.lock(); // 死锁点
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock1.unlock();
                    lock2.unlock();
                }
            });

            t1.start();
            t2.start();

            // --- 关键点 ---
            // join() 会让当前测试主线程等待 t1 和 t2 执行结束。
            // 因为发生了死锁，t1 和 t2 永远不会结束，join() 会一直阻塞。
            // 直到 5秒 时间到了，JUnit 强行终止测试并报错。
            t1.join();
            t2.join();
        }, "测试运行超时，检测到可能的死锁！");
    }
}