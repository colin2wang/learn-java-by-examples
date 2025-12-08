package com.colin.java.collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MapTest {
    private static final String[] KEYS = {"name", "age", "sex", "address", "phone", "email"};

    @Test
    public void testConcurrentHashMap() {
        log.info("Testing ConcurrentHashMap===============================");
        ConcurrentHashMap<String, String> cmap = new ConcurrentHashMap<>();
        
        // 测试添加元素
        for (String key : KEYS) {
            cmap.put(key, key);
        }
        
        // 验证大小和内容
        assertEquals(KEYS.length, cmap.size(), "ConcurrentHashMap should have " + KEYS.length + " elements");
        
        // 验证所有键值对都被正确添加
        for (String key : KEYS) {
            assertTrue(cmap.containsKey(key), "ConcurrentHashMap should contain key: " + key);
            assertEquals(key, cmap.get(key), "Value for key '" + key + "' should be '" + key + "'");
        }
        
        log.info("ConcurrentHashMap content: {}", cmap);
    }

    @Test
    public void testHashMap() {
        log.info("Testing HashMap===============================");
        HashMap<String, String> map = new HashMap<>();
        
        // 测试添加元素
        for (String key : KEYS) {
            map.put(key, key);
        }
        
        // 验证大小和内容
        assertEquals(KEYS.length, map.size(), "HashMap should have " + KEYS.length + " elements");
        
        // 验证所有键值对都被正确添加
        for (String key : KEYS) {
            assertTrue(map.containsKey(key), "HashMap should contain key: " + key);
            assertEquals(key, map.get(key), "Value for key '" + key + "' should be '" + key + "'");
        }
        
        log.info("HashMap content: {}", map);
    }

    @Test
    public void testTreeMap() {
        log.info("Testing TreeMap===============================");
        TreeMap<String, String> tmap = new TreeMap<>();
        
        // 测试添加元素
        for (String key : KEYS) {
            tmap.put(key, key);
        }
        
        // 验证大小和内容
        assertEquals(KEYS.length, tmap.size(), "TreeMap should have " + KEYS.length + " elements");
        
        // 验证所有键值对都被正确添加
        for (String key : KEYS) {
            assertTrue(tmap.containsKey(key), "TreeMap should contain key: " + key);
            assertEquals(key, tmap.get(key), "Value for key '" + key + "' should be '" + key + "'");
        }
        
        // 验证TreeMap是有序的
        assertNotNull(tmap.firstKey(), "TreeMap should have a first key");
        assertNotNull(tmap.lastKey(), "TreeMap should have a last key");
        
        log.info("TreeMap content: {}", tmap);
    }

    @Test
    public void testLinkedHashMap() {
        log.info("Testing LinkedHashMap===============================");
        LinkedHashMap<String, String> lmap = new LinkedHashMap<>();
        
        // 测试添加元素
        for (String key : KEYS) {
            lmap.put(key, key);
        }
        
        // 验证大小和内容
        assertEquals(KEYS.length, lmap.size(), "LinkedHashMap should have " + KEYS.length + " elements");
        
        // 验证所有键值对都被正确添加
        for (String key : KEYS) {
            assertTrue(lmap.containsKey(key), "LinkedHashMap should contain key: " + key);
            assertEquals(key, lmap.get(key), "Value for key '" + key + "' should be '" + key + "'");
        }
        
        log.info("LinkedHashMap content: {}", lmap);
    }
    
    @Test
    public void testHashSet() {
        log.info("Testing HashSet===============================");
        HashSet<String> set = new HashSet<>();
        
        // 测试添加元素
        for (String key : KEYS) {
            set.add(key);
        }
        
        // 验证大小和内容
        assertEquals(KEYS.length, set.size(), "HashSet should have " + KEYS.length + " elements");
        
        // 验证所有元素都被正确添加
        for (String key : KEYS) {
            assertTrue(set.contains(key), "HashSet should contain: " + key);
        }
        
        // 测试重复元素
        boolean added = set.add(KEYS[0]);
        assertFalse(added, "HashSet should not add duplicate elements");
        assertEquals(KEYS.length, set.size(), "HashSet size should not change when adding duplicates");
        
        log.info("HashSet content: {}", set);
    }
    
    @Test
    public void testLinkedHashSet() {
        log.info("Testing LinkedHashSet===============================");
        LinkedHashSet<String> lset = new LinkedHashSet<>();
        
        // 测试添加元素
        for (String key : KEYS) {
            lset.add(key);
        }
        
        // 验证大小和内容
        assertEquals(KEYS.length, lset.size(), "LinkedHashSet should have " + KEYS.length + " elements");
        
        // 验证所有元素都被正确添加
        for (String key : KEYS) {
            assertTrue(lset.contains(key), "LinkedHashSet should contain: " + key);
        }
        
        // 测试重复元素
        boolean added = lset.add(KEYS[0]);
        assertFalse(added, "LinkedHashSet should not add duplicate elements");
        assertEquals(KEYS.length, lset.size(), "LinkedHashSet size should not change when adding duplicates");
        
        log.info("LinkedHashSet content: {}", lset);
    }
}