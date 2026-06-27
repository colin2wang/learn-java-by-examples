package com.colin.java.inheritance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterfaceTest implements Calculator {

    /**
     * Test interface default method add() inherited from Calculator.
     * Principle: InterfaceTest implements Calculator and calls the inherited add() method,
     * verifying the default method returns the correct sum.
     */
    @Test
    public void testAdd() {
        assertEquals(5, add(2, 3));
    }

    /**
     * Test interface default method sub() inherited from Calculator.
     * Principle: calls the inherited sub() method to verify default method subtraction works.
     */
    @Test
    public void testSub() {
        assertEquals(1, sub(4, 3));
    }
}
