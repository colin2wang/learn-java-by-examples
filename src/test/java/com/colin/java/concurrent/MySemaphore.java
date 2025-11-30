package com.colin.java.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MySemaphore {

    public static void main(String[] args) throws InterruptedException {
        Semaphore sem1 = new Semaphore(1);
        Semaphore sem2 = new Semaphore(0);
        Semaphore sem3 = new Semaphore(0);

        final int MAX_PRINT = 9;
        AtomicInteger count = new AtomicInteger(0);

        Thread t1 = new Thread(() -> {
            try {
                sem1.acquire();
                while(true) {
                    if (count.get() >= MAX_PRINT) {
                        sem2.release();
                        break;
                    }
                    System.out.println(count.incrementAndGet());
                    sem2.release();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                sem2.acquire();
                while(true) {
                    if (count.get() >= MAX_PRINT) {
                        sem3.release();
                        break;
                    }
                    System.out.println(count.incrementAndGet());
                    sem3.release();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t3 = new Thread(() -> {
            try {
                sem3.acquire();
                while(true) {
                    if (count.get() >= MAX_PRINT) {
                        sem1.release();
                        break;
                    }
                    System.out.println(count.incrementAndGet());
                    sem1.release();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start(); t2.start(); t3.start();

        t1.join(); t2.join(); t3.join();

        System.out.println("Main thread finish");
    }
}
