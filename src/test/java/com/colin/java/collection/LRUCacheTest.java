package com.colin.java.collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    static class LRUCache {

        // 双向链表节点类
        private static class Node {
            int key;
            int value;
            Node prev;
            Node next;

            Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        private final int capacity;
        private final Map<Integer, Node> map;
        // 使用伪头和伪尾，简化边界判断
        private final Node head;
        private final Node tail;

        public LRUCache(int capacity) {
            this.capacity = capacity;
            this.map = new HashMap<>();

            // 初始化伪节点
            this.head = new Node(-1, -1);
            this.tail = new Node(-1, -1);
            // 连接伪节点
            this.head.next = this.tail;
            this.tail.prev = this.head;
        }

        public int get(int key) {
            if (!map.containsKey(key)) {
                return -1;
            }
            Node node = map.get(key);
            // 核心逻辑：被访问了，就要移动到头部
            moveToHead(node);
            return node.value;
        }

        public void put(int key, int value) {
            if (map.containsKey(key)) {
                // 1. key 存在：更新 value，移动到头部
                Node node = map.get(key);
                node.value = value;
                moveToHead(node);
            } else {
                // 2. key 不存在：创建新节点，添加到头部
                Node newNode = new Node(key, value);
                map.put(key, newNode);
                addToHead(newNode);

                // 3. 检查容量：如果超限，删除尾部（最久未使用）
                if (map.size() > capacity) {
                    Node lastNode = removeTail();
                    map.remove(lastNode.key); // 别忘了从 Map 中移除
                }
            }
        }

        // --- 以下是链表操作的 Helper Methods (体现代码整洁度) ---

        private void moveToHead(Node node) {
            removeNode(node);
            addToHead(node);
        }

        // 将节点插入到伪头之后
        private void addToHead(Node node) {
            node.prev = head;
            node.next = head.next;

            head.next.prev = node;
            head.next = node;
        }

        // 从链表中断开节点
        private void removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        // 删除真实的尾部节点（tail.prev）
        private Node removeTail() {
            Node res = tail.prev;
            removeNode(res);
            return res;
        }

        // 获取当前大小 (用于测试)
        public int size() {
            return map.size();
        }
    }

    private LRUCache lruCache;

    @BeforeEach
    void setUp() {
        // 初始化容量为 2 的缓存
        lruCache = new LRUCache(2);
    }

    @Test
    void testBasicPutAndGet() {
        lruCache.put(1, 1);
        lruCache.put(2, 2);

        assertEquals(1, lruCache.get(1));
        assertEquals(2, lruCache.get(2));
        assertEquals(-1, lruCache.get(3)); // 不存在的 key
    }

    @Test
    void testEvictionPolicy() {
        // 容量是 2
        lruCache.put(1, 1);
        lruCache.put(2, 2);

        // 此时 Cache: [2, 1] (2 是最新的)

        lruCache.put(3, 3);
        // 触发淘汰 1。Cache: [3, 2]

        assertEquals(-1, lruCache.get(1), "Key 1 应该被淘汰");
        assertEquals(2, lruCache.get(2));
        assertEquals(3, lruCache.get(3));
    }

    @Test
    void testAccessUpdatesRecency() {
        lruCache.put(1, 1);
        lruCache.put(2, 2);
        // Cache: [2, 1]

        // 访问 1，使其变成最新的
        lruCache.get(1);
        // Cache: [1, 2]

        lruCache.put(3, 3);
        // 触发淘汰 2 (因为 1 刚被访问过，受到保护)。Cache: [3, 1]

        assertEquals(-1, lruCache.get(2), "Key 2 应该被淘汰，因为 Key 1 刚被访问过");
        assertEquals(1, lruCache.get(1));
        assertEquals(3, lruCache.get(3));
    }

    @Test
    void testUpdateExistingKey() {
        lruCache.put(1, 1);
        lruCache.put(2, 2);

        // 更新 Key 1 的值
        lruCache.put(1, 10);
        // Key 1 变为最新。Cache: [1, 2]

        lruCache.put(3, 3);
        // 淘汰 2。Cache: [3, 1]

        assertEquals(10, lruCache.get(1));
        assertEquals(-1, lruCache.get(2));
        assertEquals(3, lruCache.get(3));
    }
}