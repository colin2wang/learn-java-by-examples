package com.colin.java.generics;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErasureDelayedSideEffectTest {

    /* 1. 编译期直接失败：协变赋值被拒绝 */
    // List<Integer> a = new ArrayList<>();
    // List<Number> b = a;          // ← 编译错误: incompatible types

    /* 2. 编译期通过，运行期暴露擦除的副作用 */
    @Test
    void demoErasureSideEffect() {
        List<Integer> intList = new ArrayList<>();
        intList.add(42);

        // 协变通配符让赋值通过编译
        List<? extends Number> numList = intList;

        // 但借助原始类型，我们能在运行期"作弊"
        List raw = numList;          //  unchecked conversion 警告
        raw.add(3.14);               //  把 Double 塞进原本是 List<Integer> 的容器

        /*
         * 到目前为止一切安静，泛型信息已被擦除；
         * 直到我们按 Integer 取出时才爆炸 —— 这就是"延迟副作用"
         */
        assertThrows(ClassCastException.class, () -> {
            Integer i = intList.get(1); // 3.14 无法转成 Integer
        });
    }
    
    /* 3. 演示 List<? extends Number> 中元素的实际类型 */
    @Test
    void demonstrateActualElementTypes() {
        // 创建一个存储不同数值类型的列表
        List<Number> mixedNumbers = new ArrayList<>();
        mixedNumbers.add(42);         // Integer
        mixedNumbers.add(3.14);       // Double
        mixedNumbers.add(10L);        // Long
        
        // 通过向上转型赋值给通配符类型
        List<? extends Number> wildcardList = mixedNumbers;
        
        // 验证从通配符列表中读取的元素保持其原始类型
        assertEquals(Integer.class, wildcardList.get(0).getClass());
        assertEquals(Double.class, wildcardList.get(1).getClass());
        assertEquals(Long.class, wildcardList.get(2).getClass());
        
        // 读取为Number类型是安全的
        Number num1 = wildcardList.get(0);
        Number num2 = wildcardList.get(1);
        Number num3 = wildcardList.get(2);
        
        // 即使通过通配符引用，元素仍然保持其原始类型
        assertTrue(num1 instanceof Integer);
        assertTrue(num2 instanceof Double);
        assertTrue(num3 instanceof Long);
        
        // 注意：下面的代码会导致编译错误，因为不能直接向List<? extends Number>添加元素
        // wildcardList.add(5); // 编译错误
        mixedNumbers.add(5);

        wildcardList.get(3);
    }
    
    // 辅助方法：通过类型参数化添加元素到通配符列表
    private <T extends Number> void addToWildcardList(List<T> list, T element) {
        list.add(element);
    }
}