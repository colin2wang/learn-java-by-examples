package com.colin.java.collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class HashMapCollisionTest {

    // 自定义一个键类，其 hashCode 方法总是返回相同的值
    static class DummyKey {
        private final int value;

        public DummyKey(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            // 总是返回相同的值
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DummyKey dummyKey = (DummyKey) obj;
            return value == dummyKey.value;
        }
    }

    // 自定义一个键类，其 hashCode 方法返回不同的值
    static class NormalKey {
        private final int value;

        public NormalKey(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            // 返回不同的值
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            NormalKey normalKey = (NormalKey) obj;
            return value == normalKey.value;
        }
    }

    @Test
    public void testHashMapCollisionBehavior() {
        // 创建一个 HashMap，初始容量为 16
        Map<DummyKey, String> map = new HashMap<>(16);
        final int itemCount = 1000;

        // 插入 itemCount 个键值对，所有键具有相同的 hashCode
        log.info("插入{}个具有相同hashCode的键值对...", itemCount);
        for (int i = 0; i < itemCount; i++) {
            map.put(new DummyKey(i), "Value" + i);
        }

        // 验证所有插入的值都能被正确检索
        log.info("验证所有值都能被正确检索...");
        for (int i = 0; i < itemCount; i++) {
            String value = map.get(new DummyKey(i));
            assertEquals("Value" + i, value, "键 " + i + " 的值应该能被正确检索");
        }

        // 验证 Map 的大小正确
        assertEquals(itemCount, map.size(), "Map 的大小应该为 " + itemCount);
        log.info("Map 大小验证成功: {}", map.size());

        // 演示性能差异（碰撞 vs 非碰撞）
        demonstratePerformanceDifference();
    }

    private void demonstratePerformanceDifference() {
        log.info("===== 演示哈希碰撞对性能的影响 =====");
        final int itemCount = 10000;
        
        // 使用正常键（低碰撞）测试性能
        Map<NormalKey, String> normalMap = new HashMap<>(itemCount);
        long normalPutStartTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            normalMap.put(new NormalKey(i), "Value" + i);
        }
        long normalPutEndTime = System.nanoTime();
        
        // 使用碰撞键测试性能
        Map<DummyKey, String> collisionMap = new HashMap<>(itemCount);
        long collisionPutStartTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            collisionMap.put(new DummyKey(i), "Value" + i);
        }
        long collisionPutEndTime = System.nanoTime();
        
        // 读取性能测试
        long normalGetStartTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            normalMap.get(new NormalKey(i));
        }
        long normalGetEndTime = System.nanoTime();
        
        long collisionGetStartTime = System.nanoTime();
        for (int i = 0; i < itemCount; i++) {
            collisionMap.get(new DummyKey(i));
        }
        long collisionGetEndTime = System.nanoTime();
        
        // 输出性能结果
        log.info("低碰撞场景 - Put操作耗时: {} μs", TimeUnit.NANOSECONDS.toMicros(normalPutEndTime - normalPutStartTime));
        log.info("高碰撞场景 - Put操作耗时: {} μs", TimeUnit.NANOSECONDS.toMicros(collisionPutEndTime - collisionPutStartTime));
        log.info("低碰撞场景 - Get操作耗时: {} μs", TimeUnit.NANOSECONDS.toMicros(normalGetEndTime - normalGetStartTime));
        log.info("高碰撞场景 - Get操作耗时: {} μs", TimeUnit.NANOSECONDS.toMicros(collisionGetEndTime - collisionGetStartTime));
        
        // 验证高碰撞场景确实更慢
        boolean getPerformanceDifference = (collisionGetEndTime - collisionGetStartTime) > (normalGetEndTime - normalGetStartTime);
        assertTrue(getPerformanceDifference, "高碰撞场景的Get操作应该比低碰撞场景慢");
        
        log.info("结论: 当HashMap中存在大量哈希碰撞时，性能会显著下降，因为所有元素都被存储在同一个桶的链表或红黑树中");
        log.info("在JDK 1.8及以上版本中，当链表长度超过阈值(通常是8)时，会转换为红黑树以提高查询性能");
    }
}