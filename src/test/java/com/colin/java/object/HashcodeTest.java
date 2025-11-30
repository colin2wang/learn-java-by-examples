package com.colin.java.object;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

/**
 * HashCode测试类
 * 测试Java对象的hashCode方法行为和特性
 * @author WangBing
 */
public class HashcodeTest {
    private static final Logger logger = LoggerFactory.getLogger(HashcodeTest.class);
    
    /**
     * 测试字符串hashCode碰撞
     * 验证"Aa"和"BB"具有相同的hashCode值
     */
    @Test
    public void testStringHashCodeCollision() {
        logger.info("开始测试字符串hashCode碰撞");
        
        // 计算两个字符串的hashCode
        int hashCodeAa = "Aa".hashCode();
        int hashCodeBB = "BB".hashCode();
        
        logger.info("\"Aa\"的hashCode值: {}", hashCodeAa);
        logger.info("\"BB\"的hashCode值: {}", hashCodeBB);
        
        // 验证两个不同的字符串具有相同的hashCode值（hash碰撞）
        assertEquals(hashCodeAa, hashCodeBB, "\"Aa\"和\"BB\"应该具有相同的hashCode值");
        
        // 验证它们是不同的字符串
        assertNotEquals("Aa", "BB", "\"Aa\"和\"BB\"应该是不同的字符串");
        
        logger.info("字符串hashCode碰撞测试完成");
    }
    
    /**
     * 测试对象实例hashCode的唯一性
     * 验证两个不同的对象实例通常具有不同的hashCode值
     */
    @Test
    public void testObjectHashCodeUniqueness() {
        logger.info("开始测试对象实例hashCode的唯一性");
        
        // 创建两个A类的实例
        A a = new A();
        A b = new A();
        
        // 计算hashCode值
        int hashCodeA = a.hashCode();
        int hashCodeB = b.hashCode();
        
        logger.info("对象a的hashCode值: {}", hashCodeA);
        logger.info("对象b的hashCode值: {}", hashCodeB);
        
        // 验证两个不同的对象实例具有不同的hashCode值
        // 注意：虽然理论上hashCode可能碰撞，但在实践中两个新创建的对象实例hashCode通常不同
        assertNotEquals(hashCodeA, hashCodeB, "两个不同的对象实例通常应该具有不同的hashCode值");
        
        // 验证它们是不同的对象引用
        assertNotSame(a, b, "a和b应该是不同的对象引用");
        
        logger.info("对象实例hashCode唯一性测试完成");
    }
    
    /**
     * 测试相同内容字符串的hashCode
     * 验证相同内容的字符串具有相同的hashCode值
     */
    @Test
    public void testSameStringHashCode() {
        logger.info("开始测试相同内容字符串的hashCode");
        
        // 创建相同内容的字符串
        String str1 = "test_string";
        String str2 = "test_string";
        String str3 = new String("test_string");
        
        // 计算hashCode值
        int hashCode1 = str1.hashCode();
        int hashCode2 = str2.hashCode();
        int hashCode3 = str3.hashCode();
        
        logger.info("str1的hashCode值: {}", hashCode1);
        logger.info("str2的hashCode值: {}", hashCode2);
        logger.info("str3的hashCode值: {}", hashCode3);
        
        // 验证所有相同内容的字符串具有相同的hashCode值
        assertEquals(hashCode1, hashCode2, "相同内容的字符串字面量应具有相同的hashCode值");
        assertEquals(hashCode1, hashCode3, "相同内容的字符串对象应具有相同的hashCode值");
        
        logger.info("相同内容字符串hashCode测试完成");
    }
    
    /**
     * 测试hashCode在集合中的应用
     * 验证hashCode在HashSet中的行为
     */
    @Test
    public void testHashCodeInHashSet() {
        logger.info("开始测试hashCode在HashSet中的应用");
        
        Set<A> set = new HashSet<>();
        
        // 创建两个A类的实例
        A a = new A();
        A b = new A();
        
        // 添加到集合
        boolean addedA = set.add(a);
        boolean addedB = set.add(b);
        
        logger.info("添加对象a到集合: {}", addedA);
        logger.info("添加对象b到集合: {}", addedB);
        logger.info("集合大小: {}", set.size());
        
        // 验证两个对象都被成功添加到集合中
        assertTrue(addedA, "对象a应被成功添加到集合");
        assertTrue(addedB, "对象b应被成功添加到集合");
        assertEquals(2, set.size(), "集合应包含两个不同的对象");
        
        // 验证集合包含这两个对象
        assertTrue(set.contains(a), "集合应包含对象a");
        assertTrue(set.contains(b), "集合应包含对象b");
        
        logger.info("hashCode在HashSet中的应用测试完成");
    }
    
    /**
     * 用于测试的内部类A
     */
    static class A {
    }
}