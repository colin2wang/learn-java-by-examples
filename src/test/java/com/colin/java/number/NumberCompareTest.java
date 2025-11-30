package com.colin.java.number;

import org.junit.jupiter.api.Test;

/**
 * 本测试类演示Java中Integer对象缓存机制及其对==比较运算符的影响
 */
public class NumberCompareTest {

    @Test
    void testNumberCompare() {
        /*
         * 1. 对于值为127的Integer对象：
         *    - Java为了提高性能，对-128到127范围内的整数进行了缓存
         *    - 当使用自动装箱时，如果值在缓存范围内，会返回缓存中的同一个对象
         *    - 因此a和b实际上引用的是同一个对象实例，使用==比较引用时结果为true
         */
        Integer a = 127; // 自动装箱，相当于Integer.valueOf(127)
        Integer b = 127; // 返回缓存中的同一个对象

        assert a == b; // 引用相等，因为指向同一个缓存对象

        /*
         * 2. 对于值为255的Integer对象：
         *    - 255超出了默认的Integer缓存范围（-128到127）
         *    - 每次使用自动装箱都会创建一个新的Integer对象
         *    - 因此c和d引用的是两个不同的对象实例，使用==比较引用时结果为false
         */
        Integer c = 255; // 创建新对象
        Integer d = 255; // 创建另一个新对象

        assert c != d; // 引用不相等，因为指向不同的对象
        assert c.equals(d);
        
        /*
         * 注意：如果要比较Integer对象的数值是否相等，应该使用equals()方法
         * 例如：assert c.equals(d);  // 结果为true，因为它们的数值相等
         */
    }
}