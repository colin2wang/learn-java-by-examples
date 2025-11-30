package com.colin.java.math;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Java数学函数测试类
 * 测试Java标准库中的数学操作和运算符行为
 * @author WangBing
 */
public class MathTest {
    private static final Logger logger = LoggerFactory.getLogger(MathTest.class);
    
    /**
     * 测试Math.round对负数的四舍五入处理
     * 验证Math.round(-10.5)的结果
     */
    @Test
    public void testMathRoundWithNegativeNumbers() {
        logger.info("开始测试Math.round对负数的处理");
        
        // 测试负数的四舍五入
        long result = Math.round(-10.5);
        logger.info("Math.round(-10.5) = {}", result);
        
        // 验证结果 - 注意Java中的round方法对.5的处理是向正无穷方向舍入
        // 所以-10.5会舍入到-10而不是-11
        assertEquals(-10L, result, "Math.round(-10.5)应返回-10");
        
        // 测试其他负数边界情况
        assertEquals(-11L, Math.round(-10.6), "Math.round(-10.6)应返回-11");
        assertEquals(-10L, Math.round(-10.4), "Math.round(-10.4)应返回-10");
        assertEquals(0L, Math.round(-0.4), "Math.round(-0.4)应返回0");
        assertEquals(-1L, Math.round(-0.6), "Math.round(-0.6)应返回-1");
        
        logger.info("Math.round对负数的处理测试完成");
    }
    
    /**
     * 测试Java中的^运算符（按位异或）
     * 澄清^不是幂运算符而是按位异或运算符
     */
    @Test
    public void testBitwiseXOROperator() {
        logger.info("开始测试Java中的^运算符（按位异或）");
        
        // 测试^运算符 - 注意这是按位异或，不是幂运算
        int result = 2 ^ 4;
        logger.info("2 ^ 4 = {}", result);
        
        // 验证按位异或的结果
        // 2的二进制表示: 0010
        // 4的二进制表示: 0100
        // 异或结果:     0110 (十进制的6)
        assertEquals(6, result, "2 ^ 4（按位异或）应返回6");
        
        // 对比真正的幂运算
        double powerResult = Math.pow(2, 4);
        logger.info("Math.pow(2, 4) = {}", powerResult);
        assertEquals(16.0, powerResult, 1e-10, "Math.pow(2, 4)应返回16.0");
        
        // 测试更多按位异或的例子
        assertEquals(0, 5 ^ 5, "一个数与自身异或应返回0");
        assertEquals(1, 0 ^ 1, "0与1异或应返回1");
        assertEquals(7, 3 ^ 4, "3 ^ 4应返回7");
        
        logger.info("按位异或运算符测试完成");
    }
    
    /**
     * 测试Math类的常用数学函数
     */
    @Test
    public void testCommonMathFunctions() {
        logger.info("开始测试Math类的常用函数");
        
        // 测试绝对值
        assertEquals(10, Math.abs(-10), "Math.abs(-10)应返回10");
        assertEquals(0, Math.abs(0), "Math.abs(0)应返回0");
        
        // 测试最大值和最小值
        assertEquals(5, Math.max(3, 5), "Math.max(3, 5)应返回5");
        assertEquals(3, Math.min(3, 5), "Math.min(3, 5)应返回3");
        
        // 测试平方根
        assertEquals(4.0, Math.sqrt(16), 1e-10, "Math.sqrt(16)应返回4.0");
        assertEquals(0.0, Math.sqrt(0), 1e-10, "Math.sqrt(0)应返回0.0");
        
        // 测试随机数生成
        double random = Math.random();
        assertTrue(random >= 0.0 && random < 1.0, "Math.random()应返回[0.0,1.0)范围内的值");
        
        logger.info("Math类常用函数测试完成");
    }
}