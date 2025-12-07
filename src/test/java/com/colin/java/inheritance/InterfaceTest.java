package com.colin.java.inheritance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterfaceTest implements Calculator {

    @Test
    public void testAdd() {
        assertEquals(5, add(2, 3));
    }

    @Test
    public void testSub() {
        assertEquals(1, sub(4, 3));
    }
}
