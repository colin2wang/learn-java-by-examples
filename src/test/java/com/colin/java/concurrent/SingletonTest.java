package com.colin.java.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class SingletonTest {

    static class Singleton {
        // 必须加 volatile，防止指令重排序导致其他线程获取到未初始化的对象
        private static volatile Singleton instance;
        private Singleton() {}

        public static Singleton getInstance() {
            if (instance == null) {
                synchronized (Singleton.class) {
                    if (instance == null) {
                        instance = new Singleton();
                    }
                }
            }
            return instance;
        }
    }

    /**
     * 关键步骤：重置单例状态
     * 因为单例是 static 的，JVM 加载后就会一直保留。
     * 为了测试“多线程争抢初始化”的逻辑，必须在每个 @Test 前将 instance 设回 null。
     */
    @BeforeEach
    void resetSingleton() throws Exception {
        Field instance = Singleton.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    void testBasicSingleton() {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();

        assertNotNull(s1);
        assertSame(s1, s2, "两次获取的对象应该是同一个引用");
    }

    @Test
    void testConcurrency() {
        // 模拟 100 个线程并发获取
        int threadCount = 100;
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        // 使用 Set 来存储获取到的实例，Set 会自动去重
        // 如果最终 Set 大小为 1，说明所有线程拿到的都是同一个对象
        Set<Singleton> instances = Collections.synchronizedSet(new HashSet<>());

        // 使用 CountDownLatch 作为一个"发令枪"，让所有线程尽量在同一时刻发起请求
        CountDownLatch latch = new CountDownLatch(1);

        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            for (int i = 0; i < threadCount; i++) {
                service.execute(() -> {
                    try {
                        // 所有线程在这里阻塞，等待发令
                        latch.await();
                        instances.add(Singleton.getInstance());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            // "发令枪"响，放行所有线程
            latch.countDown();

            // 等待线程池任务执行完毕（需要给一点缓冲时间让任务跑完）
            service.shutdown();
            // 这里简单等待，实际项目中可以用 awaitTermination
            while (!service.isTerminated()) {
                Thread.sleep(10);
            }
        });

        // 验证：
        // 1. 集合不为空
        // 2. 集合大小必须为 1 (说明没有任何两个线程创建了不同的实例)
        System.out.println("Instance count: " + instances.size());
        assertEquals(1, instances.size(), "单例模式失败：产生了多个实例");
    }

    /**
     * 补充测试：防止通过反射攻击破坏单例
     * (虽然你的原始代码没有防反射逻辑，但在面试中这通常是下一个问题)
     * 这个测试预期会"成功破坏"，除非你在私有构造器里加了判断。
     */
    @Test
    void testReflectionAttack() throws Exception {
        Singleton s1 = Singleton.getInstance();

        // 获取私有构造器
        Constructor<Singleton> constructor = Singleton.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        // 强制创建一个新实例
        Singleton s2 = constructor.newInstance();

        // 如果你的代码没有防反射，这里 s1 != s2
        Assertions.assertNotSame(s1, s2, "通过反射创建了新对象，破坏了单例");
    }
}