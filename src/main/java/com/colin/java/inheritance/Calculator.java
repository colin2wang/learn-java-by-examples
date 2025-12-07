package com.colin.java.inheritance;

public interface Calculator  {
    // 默认实现：加法
    default int add(int a, int b) {
        return a + b;
    }

    // 默认实现：减法
    default int sub(int a, int b) {
        return a - b;
    }
}