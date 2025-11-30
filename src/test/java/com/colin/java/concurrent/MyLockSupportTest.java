package com.colin.java.concurrent;

import java.util.concurrent.locks.LockSupport;

public class MyLockSupportTest {
    static Thread t1, t2, t3;

    static int count = 1;
    static final int MAX_COUNT = 9;

    public static void main(String[] args) throws InterruptedException {
        t1 = new Thread(() -> {
            for (int i = 1; i <= MAX_COUNT; i+=3) {
                LockSupport.park();
                System.out.printf("Thread 1: %s\n", count++);
                LockSupport.unpark(t2);
            }
        });

        t2 = new Thread(() -> {
            for (int i = 2; i <= MAX_COUNT; i+=3) {
                LockSupport.park();
                System.out.printf("Thread 2: %s\n", count++);
                LockSupport.unpark(t3);
            }
        });

        t3 = new Thread(() -> {
            for (int i = 3; i <= MAX_COUNT; i+=3) {
                LockSupport.park();
                System.out.printf("Thread 3: %s\n", count++);
                LockSupport.unpark(t1);
            }
        });

        t1.start();
        t2.start();
        t3.start();

        LockSupport.unpark(t1);

        t1.join();
        t2.join();
        t3.join();
    }
}
