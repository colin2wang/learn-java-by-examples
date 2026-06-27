package com.colin.java.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimpleLRUCacheTest {

    static class SimpleLRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        public SimpleLRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
        }
    }

    private SimpleLRUCache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        // 创建容量为3的缓存实例
        cache = new SimpleLRUCache<>(3);
    }

    /**
     * Test basic put and get on a SimpleLRUCache backed by LinkedHashMap.
     * Principle: extends LinkedHashMap with accessOrder=true and overrides removeEldestEntry()
     * to auto-evict when size > capacity; verifies basic insertion and retrieval.
     */
    @Test
    void testBasicPutAndGet() {
        // 测试基本的put和get操作
        cache.put("key1", 1);
        cache.put("key2", 2);
        cache.put("key3", 3);

        assertEquals(1, cache.get("key1"));
        assertEquals(2, cache.get("key2"));
        assertEquals(3, cache.get("key3"));
    }

    /**
     * Test LRU eviction strategy: get() refreshes access order.
     * Principle: fills capacity-3 cache, accesses key1 to move it to most-recently-used,
     * then inserts key4; verifies key2 (least recently used) is evicted.
     */
    @Test
    void testLRUEviction() {
        // 测试LRU淘汰策略
        cache.put("key1", 1);  // key1 -> key2 -> key3
        cache.put("key2", 2);
        cache.put("key3", 3);

        // 访问key1，使其成为最近使用的
        cache.get("key1");     // key2 -> key3 -> key1

        // 添加新元素，应该淘汰key2
        cache.put("key4", 4);  // key3 -> key1 -> key4

        assertNull(cache.get("key2"));  // key2应该被淘汰
        assertEquals(3, cache.get("key3"));
        assertEquals(1, cache.get("key1"));
        assertEquals(4, cache.get("key4"));
    }

    /**
     * Test that updating an existing key does not change cache size.
     * Principle: put(key1, 1) then put(key1, 100) updates the value in-place;
     * LinkedHashMap's accessOrder mode refreshes its position without eviction.
     */
    @Test
    void testUpdateExistingKey() {
        // 测试更新已存在的键
        cache.put("key1", 1);
        cache.put("key2", 2);

        // 更新key1的值
        cache.put("key1", 100);

        assertEquals(100, cache.get("key1"));
        assertEquals(2, cache.get("key2"));
        assertEquals(2, cache.size());  // 大小不应改变
    }

    /**
     * Test access-order tracking determines eviction sequence.
     * Principle: puts A,B,C then accesses A (making it most recent); adding D and E
     * evicts B then C (the two least-recently-used entries).
     */
    @Test
    void testAccessOrder() {
        // 测试访问顺序对缓存的影响
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // 按特定顺序访问
        cache.get("A");  // B -> C -> A

        // 添加两个新元素，应该淘汰B和C
        cache.put("D", 4);  // C -> A -> D
        cache.put("E", 5);  // A -> D -> E

        assertNull(cache.get("B"));
        assertNull(cache.get("C"));
        assertEquals(1, cache.get("A"));
        assertEquals(4, cache.get("D"));
        assertEquals(5, cache.get("E"));
    }

    /**
     * Test edge cases: capacity=1 cache and null values.
     * Principle: a single-entry cache evicts the only element on each new put;
     * also verifies that null values are allowed and retrievable.
     */
    @Test
    void testEdgeCases() {
        // 测试边界情况
        SimpleLRUCache<String, Integer> singleCache = new SimpleLRUCache<>(1);

        singleCache.put("only", 1);
        assertEquals(1, singleCache.get("only"));

        // 放入新元素应该淘汰唯一的元素
        singleCache.put("new", 2);
        assertNull(singleCache.get("only"));
        assertEquals(2, singleCache.get("new"));

        // 测试null值
        cache.put("nullValue", null);
        assertNull(cache.get("nullValue"));
    }
}
