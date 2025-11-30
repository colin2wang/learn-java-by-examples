package com.colin.java.concurrent;

public class BingDeadLockTest {
    private final Object resourceA = new Object();
    private final Object resourceB = new Object();

    public void testDeadlock() {
        Thread threadA = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: holding A");
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                synchronized (resourceB) {
                    System.out.println("Thread 1: holding A --> B");
                }
            }
        });

        Thread threadB = new Thread(() -> {
           synchronized (resourceB) {
               System.out.println("Thread 2: holding B");

               synchronized (resourceA) {
                   System.out.println("Thread 2: holding B --> A");
               }
           }
        });

        threadA.start();
        threadB.start();
    }

    public static void main(String[] args) {
        BingDeadLockTest deadlockTest = new BingDeadLockTest();
        deadlockTest.testDeadlock();
    }
}
