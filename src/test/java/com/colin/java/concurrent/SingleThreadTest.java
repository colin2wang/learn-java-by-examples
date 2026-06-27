package com.colin.java.concurrent;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class SingleThreadTest {

    /**
     * Test basic single-thread execution with join and timeout.
     * Principle: starts a thread that prints 1-10 with 1-second sleeps, then joins it
     * within a 5-second timeout; verifies the thread completes before the deadline.
     */
    @Test
    public void test1() {
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Thread t1 = new Thread(() -> {
                for (int i = 1; i <= 10; i++) {
                    System.out.println("t1: " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            t1.start();

            t1.join();
        }, "t1 should finish in 5 seconds");
    }
}
