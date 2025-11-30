package com.colin.java.memory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 字符串常量池测试类
 * 测试Java字符串常量池的行为和特性
 * @author WangBing
 */
public class ConstantPoolOOMTest {
    private static final Logger logger = LoggerFactory.getLogger(ConstantPoolOOMTest.class);
    
    /**
     * 测试字符串常量池的intern()方法行为
     * 验证使用intern()的字符串引用相等性
     */
    @Test
    public void testStringInternBehavior() {
        logger.info("开始测试字符串常量池intern()方法行为");
        
        // 创建两个内容相同的字符串对象
        String str1 = new String("test_intern_string");
        String str2 = new String("test_intern_string");
        
        // 验证它们是不同的对象实例
        assertFalse(str1 == str2, "new String()创建的字符串是不同的对象实例");
        assertTrue(str1.equals(str2), "new String()创建的字符串内容应该相同");
        
        // 使用intern()将字符串放入常量池
        String str1Interned = str1.intern();
        String str2Interned = str2.intern();
        
        // 验证intern()后引用相同
        assertTrue(str1Interned == str2Interned, "intern()后的字符串引用应该相同");
        logger.info("字符串常量池intern()方法测试通过");
    }
    
    /**
     * 测试字符串常量池的容量和性能
     * 生成一定数量的字符串并放入常量池，验证行为
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testStringConstantPoolCapacity() {
        logger.info("开始测试字符串常量池容量");
        
        // 使用较小的固定数量，避免OOM和测试超时
        final int MAX_ITERATIONS = 10000;
        List<String> stringList = new ArrayList<>();
        Set<String> uniqueReferences = new HashSet<>();
        
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            String newString = String.valueOf(i).intern();
            stringList.add(newString);
            uniqueReferences.add(newString);
        }
        
        logger.info("成功创建并intern了 {} 个字符串，引用集合大小: {}", 
                stringList.size(), uniqueReferences.size());
        
        // 验证集合大小
        assertEquals(MAX_ITERATIONS, stringList.size(), "字符串列表大小应等于迭代次数");
        assertEquals(MAX_ITERATIONS, uniqueReferences.size(), "唯一引用集合大小应等于迭代次数");
        
        logger.info("字符串常量池容量测试完成");
    }
    
    /**
     * 测试字符串字面量与intern()方法的关系
     * 验证字符串字面量在编译时就被放入常量池
     */
    @Test
    public void testStringLiteralsInConstantPool() {
        logger.info("开始测试字符串字面量与常量池的关系");
        
        // 字符串字面量在编译时就被放入常量池
        String literal1 = "string_literal";
        String literal2 = "string_literal";
        
        // 验证引用相同
        assertTrue(literal1 == literal2, "相同的字符串字面量引用应该相同");
        
        // 验证字面量与intern()的关系
        String newString = new String("string_literal");
        assertTrue(literal1 == newString.intern(), 
                "字符串字面量应与intern()后的同内容字符串引用相同");
        
        logger.info("字符串字面量与常量池关系测试完成");
    }
    
    /**
     * 测试常量池内存使用模式
     * 演示常量池的内存使用特性，避免无限循环导致OOM
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    public void testConstantPoolMemoryUsage() {
        logger.info("开始测试常量池内存使用模式");
        
        // 使用可控制的循环次数，模拟原测试意图但不会导致OOM
        final int BATCH_SIZE = 1000;
        final int BATCH_COUNT = 5;
        
        for (int batch = 0; batch < BATCH_COUNT; batch++) {
            List<String> batchList = new ArrayList<>();
            for (int i = 0; i < BATCH_SIZE; i++) {
                int uniqueId = batch * BATCH_SIZE + i;
                batchList.add(String.valueOf(uniqueId).intern());
            }
            
            logger.info("完成批次 {}，处理了 {} 个字符串", batch + 1, batchList.size());
            
            // 帮助GC通过清除引用
            batchList.clear();
        }
        
        logger.info("常量池内存使用模式测试完成");
    }
}