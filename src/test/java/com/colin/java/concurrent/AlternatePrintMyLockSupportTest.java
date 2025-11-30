package com.colin.java.concurrent;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class AlternatePrintMyLockSupportTest {

    // 定义三个线程引用，方便内部类访问
    static Thread t1, t2, t3;

    @Test
    void testABCPrintWithLockSupport() {
        final int MAX_PRINT = 9;
        List<Integer> result = Collections.synchronizedList(new ArrayList<>());

        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {

            // 逻辑：park自己 -> 打印 -> unpark下一个

            t1 = new Thread(() -> {
                for (int i = 1; i <= MAX_PRINT; i += 3) { // 1, 4, 7
                    LockSupport.park(); // 阻塞，等待被唤醒
                    result.add(i);
                    LockSupport.unpark(t2); // 唤醒 B
                }
            });

            t2 = new Thread(() -> {
                for (int i = 2; i <= MAX_PRINT; i += 3) { // 2, 5, 8
                    LockSupport.park();
                    result.add(i);
                    LockSupport.unpark(t3); // 唤醒 C
                }
            });

            t3 = new Thread(() -> {
                for (int i = 3; i <= MAX_PRINT; i += 3) { // 3, 6, 9
                    LockSupport.park();
                    result.add(i);
                    LockSupport.unpark(t1); // 唤醒 A
                }
            });

            t1.start();
            t2.start();
            t3.start();

            // 触发启动：主线程手动唤醒 t1，开始循环
            // 注意：LockSupport 的 unpark 如果先于 park 调用，许可会保留，
            // 所以这里不用担心 t1 还没启动这一行就执行了。
            LockSupport.unpark(t1);

            t1.join();
            t2.join();
            t3.join();
        });

        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), result);
    }
}