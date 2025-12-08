package com.colin.java.collection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MyStackTest {
    private MyStack myStack;

    @BeforeEach
    public void setUp() {
        myStack = new MyStack();
    }

    @Test
    public void testMyStackOperations() {
        // 测试空栈的情况
        assertNull(myStack.pop(1), "空栈弹出应该返回null");
        assertTrue(myStack.empty(1), "空栈的empty方法应该返回true");
        assertEquals(0, myStack.size(1), "空栈的size方法应该返回0");

        // 添加元素
        myStack.put("AAA", 1);
        myStack.put("BBB", 1);
        myStack.put("AAAA", 2);
        myStack.put("BBBB", 2);

        // 验证元素添加后的状态
        assertEquals(2, myStack.size(1), "栈1应该有2个元素");
        assertEquals(2, myStack.size(2), "栈2应该有2个元素");
        assertFalse(myStack.empty(1), "栈1非空时empty方法应该返回false");
        assertFalse(myStack.empty(2), "栈2非空时empty方法应该返回false");

        // 测试peek方法
        assertEquals("BBB", myStack.peek(1), "栈1的peek方法应该返回'BBB'");
        assertEquals("BBBB", myStack.peek(2), "栈2的peek方法应该返回'BBBB'");

        // 测试pop方法
        assertEquals("BBBB", myStack.pop(2), "栈2的pop方法应该返回'BBBB'");
        assertEquals(1, myStack.size(2), "栈2在pop后应该有1个元素");

        // 添加更多元素
        myStack.put("CCCC", 2);
        myStack.put("CCC", 1);
        assertEquals(3, myStack.size(1), "栈1在添加后应该有3个元素");
        assertEquals(2, myStack.size(2), "栈2在添加后应该有2个元素");

        // 验证弹出顺序（LIFO）
        log.info("弹出栈1的第一个元素: {}", myStack.pop(1));
        log.info("弹出栈1的第二个元素: {}", myStack.pop(1));
        log.info("弹出栈1的第三个元素: {}", myStack.pop(1));

        // 验证弹出后的状态
        assertTrue(myStack.empty(1), "栈1在全部弹出后应该为空");
    }

    // 内部的MyStack实现类
    static class MyStack {
        Object values[];
        int flag[];
        int index = 0;

        public MyStack() {
            flag = new int[300];
            values = new Object[300];
        }

        public int size(int num) { // 0, 1, 2
            int count = 0;
            for (int i = 0; i < index; i++) {
                if (flag[i] == num) {
                    count++;
                }
            }
            return count;
        }

        public boolean empty(int num) {
            return size(num) == 0;
        }

        public void put(Object obj, int num) {
            flag[index] = num;
            values[index++] = obj;
        }

        public Object pop(int num) {
            Object result = null;
            int mIndex = 0;
            for (int i = index - 1; i >= 0; i--) {
                if (flag[i] == num) {
                    result = values[i];
                    mIndex = i;
                    index--;
                    break;
                }
            }

            // 重新排列数组，因为从中间删除了一个元素
            for (int i = mIndex; i < index; i++) {
                flag[i] = flag[i + 1];
                values[i] = values[i + 1];
            }

            return result;
        }

        public Object peek(int num) {
            for (int i = index - 1; i >= 0; i--) {
                if (flag[i] == num) {
                    return values[i];
                }
            }
            return null;
        }
    }
}