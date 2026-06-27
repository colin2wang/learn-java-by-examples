
package com.colin.java.stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试类：展示Java 8函数式接口的各种用法
 * 包括自定义函数式接口、标准函数式接口、Lambda表达式和方法引用
 */
public class FunctionalInterfaceTest {
    private static final Logger logger = LoggerFactory.getLogger(FunctionalInterfaceTest.class);
	
    /**
     * 自定义函数式接口示例
     * 使用@FunctionalInterface注解确保接口只有一个抽象方法
     */
	@FunctionalInterface
	interface Converter<F, T> {
		T convert(F from);
	}

    /**
     * 带默认方法的函数式接口
     */
    @FunctionalInterface
    interface EnhancedConverter<F, T> {
        T convert(F from);
        
        // 默认方法不影响函数式接口的有效性
        default String getDescription() {
            return "A converter that transforms from one type to another";
        }
        
        // 静态方法也不影响函数式接口的有效性
        static <F, T> EnhancedConverter<F, T> of(Converter<F, T> converter) {
            return converter::convert;
        }
    }
    
    /**
     * Test basic Lambda expression creating a custom functional interface instance.
     * Principle: instantiates Converter<String, Integer> via Lambda (from) -> Integer.valueOf(from),
     * demonstrating @FunctionalInterface with a single abstract method.
     */
	@Test
	public void testBasicLambdaExpression() {
        logger.info("Testing basic lambda expression for custom functional interface");
		Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
		Integer converted = converter.convert("123");
        logger.info("Converted value: {}", converted);
        assertEquals(123, converted, "Should convert string to correct integer value");
	}

    /**
     * Test method reference as a shorthand for Lambda expression.
     * Principle: uses Integer::valueOf instead of (from) -> Integer.valueOf(from),
     * showing that method references are syntactic sugar for single-method Lambdas.
     */
    @Test
    public void testMethodReference() {
        logger.info("Testing method reference");
        // 方法引用是Lambda表达式的简化形式
        Converter<String, Integer> converter = Integer::valueOf;
        Integer converted = converter.convert("456");
        logger.info("Converted using method reference: {}", converted);
        assertEquals(456, converted, "Should convert string to correct integer value using method reference");
    }
    
    /**
     * Test standard Java 8 BiFunction functional interface.
     * Principle: creates a BiFunction<Integer, Integer, Integer> that adds two integers,
     * demonstrating a built-in functional interface that takes two arguments.
     */
    @Test
    public void testStandardFunctionalInterface() {
        logger.info("Testing standard Java 8 functional interface - BiFunction");
        // BiFunction接受两个参数并产生一个结果
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        Integer result = add.apply(10, 20);
        logger.info("Result of BiFunction: {}", result);
        assertEquals(30, result, "BiFunction should correctly sum two integers");
    }
    
    /**
     * Test functional interface with a default method.
     * Principle: EnhancedConverter has both an abstract convert() and a default getDescription();
     * the Lambda implements only the abstract method while the default method is inherited.
     */
    @Test
    public void testDefaultMethod() {
        logger.info("Testing functional interface with default method");
        EnhancedConverter<String, String> toUpperCase = String::toUpperCase;
        
        // 调用抽象方法
        String result = toUpperCase.convert("hello");
        logger.info("Converted string: {}", result);
        assertEquals("HELLO", result, "Should convert string to uppercase");
        
        // 调用默认方法
        String description = toUpperCase.getDescription();
        logger.info("Default method description: {}", description);
        assertTrue(description.contains("transforms"), "Default method should return expected description");
    }
    
    /**
     * Test static factory method in a functional interface.
     * Principle: EnhancedConverter.of() wraps a basic Converter into an EnhancedConverter
     * via a static method, demonstrating that static methods don't affect functional interface status.
     */
    @Test
    public void testStaticMethod() {
        logger.info("Testing static method in functional interface");
        // 使用静态工厂方法创建函数式接口实例
        Converter<String, Integer> originalConverter = Integer::valueOf;
        EnhancedConverter<String, Integer> enhancedConverter = EnhancedConverter.of(originalConverter);
        
        Integer result = enhancedConverter.convert("789");
        logger.info("Result using static method factory: {}", result);
        assertEquals(789, result, "Should correctly convert string using static method factory");
    }
    
    /**
     * Test Consumer functional interface — consumes a value without returning.
     * Principle: Consumer<String> accepts a string and appends " processed" to a StringBuilder
     * as a side effect, demonstrating the accept() method contract.
     */
    @Test
    public void testConsumerInterface() {
        logger.info("Testing Consumer functional interface");
        StringBuilder sb = new StringBuilder();
        
        // Consumer接受一个输入参数并且不返回任何结果
        Consumer<String> consumer = s -> sb.append(s).append(" processed");
        consumer.accept("Test string");
        
        logger.info("Consumer result: {}", sb.toString());
        assertEquals("Test string processed", sb.toString(), "Consumer should append text to StringBuilder");
    }
    
    /**
     * Test Predicate functional interface — returns a boolean result.
     * Principle: Predicate<String> tests if length > 5; applies test() to two strings
     * and asserts true/false results, demonstrating the test() method contract.
     */
    @Test
    public void testPredicateInterface() {
        logger.info("Testing Predicate functional interface");
        
        // Predicate接受一个输入参数，返回一个布尔值结果
        Predicate<String> isLongerThan5 = s -> s.length() > 5;
        
        boolean result1 = isLongerThan5.test("Hello World");
        boolean result2 = isLongerThan5.test("Hi");
        
        logger.info("'Hello World' is longer than 5: {}", result1);
        logger.info("'Hi' is longer than 5: {}", result2);
        
        assertTrue(result1, "'Hello World' should be longer than 5 characters");
        assertFalse(result2, "'Hi' should not be longer than 5 characters");
    }
    
    /**
     * Test Predicate via bound method reference in collection context.
     * Principle: "A"::startsWith creates a Predicate that tests if the argument starts with "A",
     * demonstrating how bound method references capture the receiver at creation time.
     */
    @Test
    public void testFunctionalInterfaceInCollection() {
        logger.info("Testing functional interface in collection context");
        
        // 展示函数式接口在集合操作中的使用（通过String::startsWith方法引用）
        Predicate<String> startsWithA = "A"::startsWith;
        
        boolean result1 = startsWithA.test("Apple");
        boolean result2 = startsWithA.test("Banana");
        
        logger.info("'Apple' starts with 'A': {}", result1);
        logger.info("'Banana' starts with 'A': {}", result2);
        
        assertTrue(result1, "'Apple' should start with 'A'");
        assertFalse(result2, "'Banana' should not start with 'A'");
    }
}