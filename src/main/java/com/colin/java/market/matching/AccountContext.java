package com.colin.java.market.matching;

// 模拟账户状态（内存中）
public class AccountContext {
    public static long balance = 1_000_000L; // 模拟余额
    public static long dailyNetLimit = 100_000L; // 单日净额限制
    public static long currentDailyNet = 0L;
}
