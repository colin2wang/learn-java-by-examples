package com.colin.java.extension;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ClassExtendTest {

    static class A {
    }

    static class B extends A {
    }

    public static void call(A a) {
        System.out.println("Call A");
    }
    
    public static void call(B b) {
        System.out.println("Call B");
    }

    @Test
    void testStaticMethodBindingWithPolymorphicReference() {
        log.info("Testing static method binding with polymorphic reference");
        
        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Test case 1: Polymorphic reference (declared as A, instantiated as B)
            A a = new B();
            call(a);

            // Verify output - should call call(A) due to static binding
            String output = outputStream.toString().trim();
            assertEquals("Call A", output,
                    "When calling with a polymorphic reference (A a = new B()), static method call(A) should be invoked");
            log.info("Output verified: {}", output);
        } finally {
            // Restore System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void testStaticMethodBindingWithConcreteType() {
        log.info("Testing static method binding with concrete type");
        
        // Redirect System.out to capture output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Test case 2: Direct B instance
            call(new B());

            // Verify output - should call call(B) due to static binding
            String output = outputStream.toString().trim();
            assertEquals("Call B", output,
                    "When calling with a direct B instance, static method call(B) should be invoked");
            log.info("Output verified: {}", output);
        } finally {
            // Restore System.out
            System.setOut(originalOut);
        }
    }

    @Test
    void testMethodOverloadingResolution() {
        log.info("Testing method overloading resolution");
        
        // This test verifies Java's method overloading resolution rules
        // Create instances of both types
        A aInstance = new A();
        B bInstance = new B();
        A polymorphicAB = new B();
        
        // Test with A instance
        ByteArrayOutputStream outputA = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputA));
        call(aInstance);
        assertEquals("Call A", outputA.toString().trim());
        log.info("A instance calls call(A): verified");

        // Test with B instance
        ByteArrayOutputStream outputB = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputB));
        call(bInstance);
        assertEquals("Call B", outputB.toString().trim());
        log.info("B instance calls call(B): verified");

        // Test with polymorphic reference
        ByteArrayOutputStream outputPolymorphic = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputPolymorphic));
        call(polymorphicAB);
        assertEquals("Call A", outputPolymorphic.toString().trim());
        log.info("Polymorphic reference (A) calls call(A): verified");

        // Restore System.out
        System.setOut(new PrintStream(new ByteArrayOutputStream())); // Avoid affecting other tests
    }
}