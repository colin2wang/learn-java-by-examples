package com.colin.java.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试Java 8 Lambda表达式的各种特性和用法
 */
public class LambdaTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LambdaTest.class);
	
	/**
	 * Test pre-Lambda sorting with anonymous Comparator inner class.
	 * Principle: demonstrates the verbose pre-Java-8 approach of implementing Comparator
	 * via an anonymous class, then verifies the list is sorted in descending order.
	 */
	@Test
	public void testNoLambda() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		Collections.sort(names, new Comparator<String>() {
		    @Override
		    public int compare(String a, String b) {
		        return b.compareTo(a);
		    }
		});
		
		LOGGER.info("After call sort() with anonymous class, name: {}", names);
		Assertions.assertEquals(Arrays.asList("xenia", "peter", "mike", "anna"), names);
	}
	
	/**
	 * Test full-syntax Lambda expression for sorting.
	 * Principle: replaces anonymous class with an explicit-type Lambda (String a, String b) -> {},
	 * demonstrating the basic Lambda syntax introduced in Java 8.
	 */
	@Test
	public void testLambda() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		Collections.sort(names, (String a, String b) -> {
		    return b.compareTo(a);
		});
		
		LOGGER.info("After call sort() with lambda, name: {}", names);
		Assertions.assertEquals(Arrays.asList("xenia", "peter", "mike", "anna"), names);
	}
	
	/**
	 * Test simplified Lambda with type inference and single expression.
	 * Principle: removes explicit type parameters and curly braces, relying on the compiler
	 * to infer types from the Collections.sort() context.
	 */
	@Test
	public void testSimplifiedLambda() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		Collections.sort(names, (a, b) -> b.compareTo(a)); // 简化语法
		
		LOGGER.info("After call sort() with simplified lambda, name: {}", names);
		Assertions.assertEquals(Arrays.asList("xenia", "peter", "mike", "anna"), names);
	}
	
	/**
	 * Test method reference syntax as Lambda shorthand.
	 * Principle: uses String::compareToIgnoreCase as a method reference, which is
	 * syntactic sugar for (a, b) -> a.compareToIgnoreCase(b).
	 */
	@Test
	public void testMethodReference() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		Collections.sort(names, String::compareToIgnoreCase); // 方法引用
		
		LOGGER.info("After call sort() with method reference, name: {}", names);
		Assertions.assertEquals(Arrays.asList("anna", "mike", "peter", "xenia"), names);
	}
	
	/**
	 * Test Stream.filter() with a Lambda predicate.
	 * Principle: creates a stream from a list, filters elements where name starts with "m",
	 * and collects the result to verify only "mike" remains.
	 */
	@Test
	public void testLambdaWithStreamFilter() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		List<String> filteredNames = names.stream()
			                            .filter(name -> name.startsWith("m"))
			                            .collect(Collectors.toList());
		
		LOGGER.info("Filtered names starting with 'm': {}", filteredNames);
		Assertions.assertEquals(Arrays.asList("mike"), filteredNames);
	}
	
	/**
	 * Test Stream.map() with a method reference to transform elements.
	 * Principle: maps each string to its uppercase form via String::toUpperCase,
	 * demonstrating functional transformation in a stream pipeline.
	 */
	@Test
	public void testLambdaWithStreamMap() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		List<String> upperCaseNames = names.stream()
			                             .map(String::toUpperCase)
			                             .collect(Collectors.toList());
		
		LOGGER.info("Names in uppercase: {}", upperCaseNames);
		Assertions.assertEquals(Arrays.asList("PETER", "ANNA", "MIKE", "XENIA"), upperCaseNames);
	}
	
	/**
	 * Test Consumer functional interface with a Lambda.
	 * Principle: a Consumer<String> appends each name to a StringBuilder via forEach(),
	 * demonstrating a side-effect-only Lambda that consumes values without returning.
	 */
	@Test
	public void testConsumerLambda() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		StringBuilder sb = new StringBuilder();
		
		Consumer<String> action = name -> sb.append(name).append(", ");
		names.forEach(action);
		
		LOGGER.info("Concatenated names: {}", sb.toString());
		Assertions.assertTrue(sb.toString().contains("peter, anna, mike, xenia, "));
	}
	
	/**
	 * Test Function functional interface with a Lambda.
	 * Principle: creates a Function<String, Integer> that maps a string to its length,
	 * demonstrating a transformational Lambda that maps input to output.
	 */
	@Test
	public void testFunctionLambda() {
		Function<String, Integer> stringLength = s -> s.length();
		int length = stringLength.apply("Hello Lambda");
		
		LOGGER.info("Length of 'Hello Lambda': {}", length);
		Assertions.assertEquals(12, length);
	}
	
	/**
	 * Test Predicate functional interface with a Lambda.
	 * Principle: creates a Predicate<String> that tests s.length() > 5, then applies it
	 * to "Lambda" to verify the boolean result.
	 */
	@Test
	public void testPredicateLambda() {
		Predicate<String> isLongerThanFive = s -> s.length() > 5;
		boolean result = isLongerThanFive.test("Lambda");
		
		LOGGER.info("Is 'Lambda' longer than 5 characters? {}", result);
		Assertions.assertTrue(result);
	}
	
	/**
	 * Test chaining multiple Lambda operations in a Stream pipeline.
	 * Principle: combines filter (length > 4), map (toUpperCase), and sorted() in a single
	 * stream chain to demonstrate functional composition with Lambdas.
	 */
	@Test
	public void testMultipleLambdasInCollection() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia", "john", "alice");
		
		List<String> result = names.stream()
			                     .filter(name -> name.length() > 4)  // 过滤长度小于4的名字
			                     .map(String::toUpperCase)          // 转换为大写
			                     .sorted()                          // 排序
			                     .collect(Collectors.toList());
		
		LOGGER.info("Processed names: {}", result);
		Assertions.assertEquals(Arrays.asList("ALICE", "PETER", "XENIA"), result);
	}
}