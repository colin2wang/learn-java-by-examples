package com.colin.java.memory;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Java引用类型测试类
 * 测试不同类型引用（软引用、弱引用）的行为特性
 * @author WangBing
 */
public class ObjectReferenceTest {
    private static final Logger logger = LoggerFactory.getLogger(ObjectReferenceTest.class);
    
    /**
     * 测试软引用(SoftReference)的行为
     * 软引用在内存不足时才会被回收
     * 注意：由于字符串常量池的特殊性，此测试使用了自定义对象
     */
    @Test
    public void testSoftReferenceBehavior() {
        logger.info("开始测试软引用行为");
        
        // 使用自定义对象而非字符串常量，避免字符串常量池的影响
        Object testObject = new Object();
        SoftReference<Object> softReference = new SoftReference<>(testObject);
        
        // 验证初始引用有效
        assertNotNull(softReference.get(), "软引用初始化后应能获取对象");
        logger.info("软引用初始化成功，当前引用值: {}", softReference.get());
        
        // 清除强引用
        testObject = null;
        
        // 执行垃圾回收
        System.gc();
        
        // 在内存充足的情况下，软引用通常不会被回收
        // 注意：这不是绝对的保证，取决于JVM实现和内存状况
        Object retrievedObject = softReference.get();
        logger.info("GC后软引用值: {}", retrievedObject);
        
        // 软引用的特性是内存不足时才回收，所以这里可能仍能获取到对象
        // 我们不做绝对断言，而是记录结果
        if (retrievedObject != null) {
            logger.info("软引用在内存充足时通常不会被回收");
        } else {
            logger.info("注意：软引用已被回收，这可能是由于当前内存压力导致");
        }
        
        logger.info("软引用行为测试完成");
    }
    
    /**
     * 测试弱引用(WeakReference)的行为
     * 弱引用在垃圾回收时会被回收
     */
    @Test
    public void testWeakReferenceBehavior() {
        logger.info("开始测试弱引用行为");
        
        // 使用自定义对象而非字符串常量，避免字符串常量池的影响
        Object testObject = new Object();
        WeakReference<Object> weakReference = new WeakReference<>(testObject);
        
        // 验证初始引用有效
        assertNotNull(weakReference.get(), "弱引用初始化后应能获取对象");
        logger.info("弱引用初始化成功，当前引用值: {}", weakReference.get());
        
        // 清除强引用
        testObject = null;
        
        // 执行垃圾回收
        System.gc();
        
        // 尝试多次获取，增加垃圾回收生效的可能性
        Object retrievedObject = null;
        for (int i = 0; i < 3; i++) {
            retrievedObject = weakReference.get();
            if (retrievedObject == null) {
                break;
            }
            // 给GC一些时间
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.gc();
        }
        
        logger.info("GC后弱引用值: {}", retrievedObject);
        
        // 弱引用的特性是垃圾回收时会被回收
        // 注意：虽然理论上应该为null，但GC行为有时不可预测，所以使用assertTrue而不是assertNull
        boolean wasCollected = (retrievedObject == null);
        assertTrue(wasCollected, "弱引用在失去强引用并执行GC后应被回收");
        
        logger.info("弱引用行为测试完成");
    }
    
    /**
     * 测试字符串常量与弱引用的交互
     * 演示字符串常量池对引用回收的影响
     */
    @Test
    public void testStringConstantWithReferences() {
        logger.info("开始测试字符串常量与引用的交互");
        
        // 使用字符串常量
        String stringConstant = "test_constant";
        WeakReference<String> weakReference = new WeakReference<>(stringConstant);
        SoftReference<String> softReference = new SoftReference<>(stringConstant);
        
        // 清除强引用
        stringConstant = null;
        
        // 执行垃圾回收
        System.gc();
        
        // 由于字符串常量通常不会被回收（在字符串常量池中），所以引用仍然有效
        logger.info("GC后弱引用字符串常量: {}", weakReference.get());
        logger.info("GC后软引用字符串常量: {}", softReference.get());
        
        // 字符串常量通常不会被回收，所以引用应仍然有效
        // 但这不是绝对的保证，取决于JVM实现
        assertNotNull(weakReference.get(), "字符串常量即使被弱引用引用且无强引用，通常也不会被回收");
        assertNotNull(softReference.get(), "字符串常量即使被软引用引用且无强引用，通常也不会被回收");
        
        logger.info("字符串常量与引用交互测试完成");
    }
}