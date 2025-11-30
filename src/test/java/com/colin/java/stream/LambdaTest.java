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
	 * 测试使用匿名内部类实现Comparator接口（Lambda之前的方式）
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
	 * 测试使用完整语法的Lambda表达式
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
	 * 测试使用简化语法的Lambda表达式（类型推断和单表达式）
	 */
	@Test
	public void testSimplifiedLambda() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		Collections.sort(names, (a, b) -> b.compareTo(a)); // 简化语法
		
		LOGGER.info("After call sort() with simplified lambda, name: {}", names);
		Assertions.assertEquals(Arrays.asList("xenia", "peter", "mike", "anna"), names);
	}
	
	/**
	 * 测试方法引用（Method References）
	 */
	@Test
	public void testMethodReference() {
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");
		Collections.sort(names, String::compareToIgnoreCase); // 方法引用
		
		LOGGER.info("After call sort() with method reference, name: {}", names);
		Assertions.assertEquals(Arrays.asList("anna", "mike", "peter", "xenia"), names);
	}
	
	/**
	 * 测试使用Lambda表达式与Stream API过滤集合
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
	 * 测试使用Lambda表达式与Stream API转换集合
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
	 * 测试函数式接口Consumer的Lambda表达式
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
	 * 测试函数式接口Function的Lambda表达式
	 */
	@Test
	public void testFunctionLambda() {
		Function<String, Integer> stringLength = s -> s.length();
		int length = stringLength.apply("Hello Lambda");
		
		LOGGER.info("Length of 'Hello Lambda': {}", length);
		Assertions.assertEquals(12, length);
	}
	
	/**
	 * 测试函数式接口Predicate的Lambda表达式
	 */
	@Test
	public void testPredicateLambda() {
		Predicate<String> isLongerThanFive = s -> s.length() > 5;
		boolean result = isLongerThanFive.test("Lambda");
		
		LOGGER.info("Is 'Lambda' longer than 5 characters? {}", result);
		Assertions.assertTrue(result);
	}
	
	/**
	 * 测试在集合中使用多个Lambda表达式
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