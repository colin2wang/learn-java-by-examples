package com.colin.java.collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ConnectionTest {

	@Test
	public void testArrayReinitialization() {        
		// 测试数组重新初始化
		int inta[] = new int[10];
		inta = new int[20];
		assertEquals(20, inta.length, "Array length should be 20 after reinitialization");
		log.info("Array content: {}", Arrays.toString(inta));
	}

	@Test
	public void testListOperations() {
		// 测试List操作
		List<String> list = Arrays.asList("Colin", "is", "Here");
		assertEquals(3, list.size(), "List should contain 3 elements");
		assertTrue(list.contains("Colin"), "List should contain 'Colin'");
		assertTrue(list.contains("is"), "List should contain 'is'");
		assertTrue(list.contains("Here"), "List should contain 'Here'");
		log.info("List content: {}", list);
	}
	
	@Test
	public void testSetConversion() {
		// 测试Set转换
		List<String> list = Arrays.asList("Colin", "is", "Here");
		Set<String> set = new HashSet<>(list);
		assertEquals(3, set.size(), "Set should contain 3 elements");
		assertTrue(set.contains("Colin"), "Set should contain 'Colin'");
		log.info("Set content: {}", set);
	}
	
	@Test
	public void testSetToListConversion() {
		// 测试转回List
		List<String> originalList = Arrays.asList("Colin", "is", "Here");
		Set<String> set = new HashSet<>(originalList);
		List<String> newList = new ArrayList<>(set);
		assertEquals(3, newList.size(), "New list should contain 3 elements");
		assertTrue(newList.contains("Colin"), "New list should contain 'Colin'");
		log.info("New list content: {}", newList);
	}
}