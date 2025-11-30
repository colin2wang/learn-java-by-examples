package com.colin.java.bitmap;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SnowflakeConverter {
    private final ConcurrentHashMap<Long, Integer> sf2Int = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    public int encode(long snowflake) {
        return sf2Int.computeIfAbsent(snowflake, k -> counter.getAndIncrement());
    }

    public Integer get(long snowflake) {
        return sf2Int.get(snowflake);
    }
}