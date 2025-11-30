package com.colin.java.calculate;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 计算相关的测试类
 * 包含概率计算和字符统计等测试
 * @author WangBing
 */
public class CalculateTest {
    private static final Logger logger = LoggerFactory.getLogger(CalculateTest.class);
    final float MU = 0.01f;
    
    /**
     * 计算概率的辅助方法
     * @param x 分子部分的整数
     * @param y 分母部分的整数
     * @return 计算得到的概率值
     */
    static float getP(int x, int y) {
        return (x + 0.01f / 2) / (y + 0.01f);
    }

    /**
     * 测试概率计算方法
     * 验证getP方法在不同参数下的计算结果
     */
    @Test
    public void testProbabilityCalculation() {
        logger.info("开始测试概率计算方法");
        
        // 测试不同参数组合的概率计算
        float result1 = CalculateTest.getP(4, 5);
        float result2 = CalculateTest.getP(1, 5);
        float result3 = CalculateTest.getP(2, 5);
        float result4 = CalculateTest.getP(3, 5);
        float result5 = CalculateTest.getP(5, 10);
        
        logger.info("P(4,5) = {}", result1);
        logger.info("P(1,5) = {}", result2);
        logger.info("P(2,5) = {}", result3);
        logger.info("P(3,5) = {}", result4);
        logger.info("P(5,10) = {}", result5);
        
        // 验证概率值在合理范围内 [0, 1]
        assertTrue(result1 >= 0 && result1 <= 1, "概率值应在0到1之间");
        assertTrue(result2 >= 0 && result2 <= 1, "概率值应在0到1之间");
        assertTrue(result3 >= 0 && result3 <= 1, "概率值应在0到1之间");
        assertTrue(result4 >= 0 && result4 <= 1, "概率值应在0到1之间");
        assertTrue(result5 >= 0 && result5 <= 1, "概率值应在0到1之间");
        
        // 验证概率计算的单调性
        assertTrue(result1 > result2, "P(4,5)应大于P(1,5)");
        assertTrue(result2 < result3, "P(1,5)应小于P(2,5)");
        assertTrue(result3 < result4, "P(2,5)应小于P(3,5)");
        assertTrue(result4 < result1, "P(3,5)应小于P(4,5)");
        
        logger.info("概率计算方法测试完成");
    }

    /**
     * 测试乘法计算
     * 验证多个小数相乘的结果
     */
    @Test
    public void testMultiplicationCalculation() {
        logger.info("开始测试乘法计算");
        
        // 测试多个小数相乘的精度和结果
        double product1 = 0.7994 * 0.7994 * 0.4002 * 0.5998;
        double product2 = 96 / 625f;
        double product3 = 0.4002 * 0.2006 * 0.7994 * 0.2006;
        double product4 = 0.1534 * 0.5;
        double product5 = 0.0129 * 0.5;
        
        logger.info("乘积1 = {}", product1);
        logger.info("乘积2 = {}", product2);
        logger.info("乘积3 = {}", product3);
        logger.info("乘积4 = {}", product4);
        logger.info("乘积5 = {}", product5);
        
        // 验证乘积在合理范围内
        assertTrue(product1 >= 0, "乘积应大于等于0");
        assertTrue(product2 >= 0, "乘积应大于等于0");
        assertTrue(product3 >= 0, "乘积应大于等于0");
        assertTrue(product4 >= 0, "乘积应大于等于0");
        assertTrue(product5 >= 0, "乘积应大于等于0");
        
        // 验证特定计算的结果范围
        assertTrue(product1 < 1, "多个小于1的数相乘应小于1");
        assertTrue(product3 < 1, "多个小于1的数相乘应小于1");
        
        logger.info("乘法计算测试完成");
    }

    /**
     * 测试大数除法计算
     * 验证大数除法的精度和结果
     */
    @Test
    public void testLargeNumberDivision() {
        logger.info("开始测试大数除法计算");
        
        double d1 = 1800.0 / 2e8;
        double d2 = 5 * 20 / 1e7;
        double sum = d1 + d2;
        
        logger.info("d1 = {}", d1);
        logger.info("d2 = {}", d2);
        logger.info("d1 + d2 = {}", sum);
        
        // 验证结果的正确性
        assertEquals(9.0e-6, d1, 1e-10, "1800.0 / 2e8 计算错误");
        assertEquals(1.0e-5, d2, 1e-10, "5 * 20 / 1e7 计算错误");
        assertEquals(1.9e-5, sum, 1e-10, "d1 + d2 计算错误");
        
        logger.info("大数除法计算测试完成");
    }

    /**
     * 测试字符数组操作
     * 验证字符数组的比较和字符统计功能
     */
    @Test
    public void testCharArrayOperations() {
        logger.info("开始测试字符数组操作");
        
        char[] ch1 = { 'M', 'O', 'N', 'K', 'E', 'Y' };
        char[] ch2 = { 'D', 'O', 'N', 'K', 'E', 'Y' };
        char[] ch3 = { 'M', 'A', 'K', 'E' };
        char[] ch4 = { 'M', 'U', 'C', 'K', 'Y' };
        char[] ch5 = { 'C', 'O', 'K', 'I', 'E' };
        
        // 验证数组长度
        assertEquals(6, ch1.length, "ch1数组长度应为6");
        assertEquals(6, ch2.length, "ch2数组长度应为6");
        assertEquals(4, ch3.length, "ch3数组长度应为4");
        assertEquals(5, ch4.length, "ch4数组长度应为5");
        assertEquals(5, ch5.length, "ch5数组长度应为5");
        
        // 验证特定位置的字符
        assertEquals('M', ch1[0], "ch1[0]应为'M'");
        assertEquals('D', ch2[0], "ch2[0]应为'D'");
        assertEquals('M', ch3[0], "ch3[0]应为'M'");
        
        // 计算特定字符的出现次数
        int kCountInAllArrays = countCharInAllArrays('K', ch1, ch2, ch3, ch4, ch5);
        int eCountInAllArrays = countCharInAllArrays('E', ch1, ch2, ch3, ch4, ch5);
        
        logger.info("所有数组中'K'的出现次数: {}", kCountInAllArrays);
        logger.info("所有数组中'E'的出现次数: {}", eCountInAllArrays);
        
        // 验证字符统计结果
        assertEquals(5, kCountInAllArrays, "所有数组中'K'的出现次数应为5");
        assertEquals(4, eCountInAllArrays, "所有数组中'E'的出现次数应为3");
        
        // 比较字符串
        String str1 = new String(ch1);
        String str2 = new String(ch2);
        assertFalse(str1.equals(str2), "ch1和ch2转换为字符串后不应相等");
        assertTrue(str1.contains("KEY"), "ch1应包含'KEY'");
        assertTrue(str2.contains("KEY"), "ch2应包含'KEY'");
        
        logger.info("字符数组操作测试完成");
    }
    
    /**
     * 计算多个字符数组中特定字符的出现次数
     * @param targetChar 目标字符
     * @param arrays 字符数组列表
     * @return 目标字符的出现次数
     */
    private int countCharInAllArrays(char targetChar, char[]... arrays) {
        int count = 0;
        for (char[] array : arrays) {
            for (char c : array) {
                if (c == targetChar) {
                    count++;
                }
            }
        }
        return count;
    }
}