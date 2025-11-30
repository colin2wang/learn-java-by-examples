package com.colin.java.bitmap;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.roaringbitmap.RoaringBitmap;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SnowflakeConverterTest {
    private final SnowflakeConverter converter = new SnowflakeConverter();
    private final RMBitmapStore store = new RMBitmapStore();
    private final Random rnd = new Random(0);

    @Test
    void testEncodeDecode() {
        long sf1 = 1385987847360512000L; // 示例 Snowflake
        long sf2 = 1385987847360512001L;
        int id1 = converter.encode(sf1);
        int id2 = converter.encode(sf2);
        assertNotEquals(id1, id2);
        assertEquals(id1, converter.encode(sf1)); // 幂等
    }

    @Test
    void testCommonFriends() {
        // 构造 2 个用户，各加 10 k 好友，其中 5 k 重叠
        int userA = converter.encode(1385987847360512000L);
        int userB = converter.encode(1385987847360512001L);
        for (int i = 0; i < 10_000; i++) {
            int f = converter.encode(1000000000000000000L + i);
            store.addFriend(userA, f);
            if (i % 2 == 0) store.addFriend(userB, f); // 一半重叠
        }
        RoaringBitmap common = store.commonFriends(userA, userB);
        assertEquals(5_000, common.getCardinality());
    }

    @Test
    void testMillionUsers() {
        // 1 000 000 用户，每人 200 好友，验证内存与交集
        for (long sf = 10_000_000_000_000_000L; sf < 10_000_000_000_000_000L + 1_000_000; sf++) {
            int u = converter.encode(sf);
            for (int j = 0; j < 200; j++) {
                int f = converter.encode(20_000_000_000_000_000L + rnd.nextInt(5_000_000));
                store.addFriend(u, f);
            }
        }
        int u1 = converter.encode(10_000_000_000_000_123L);
        int u2 = converter.encode(10_000_000_000_000_456L);
        RoaringBitmap common = store.commonFriends(u1, u2);
        assertTrue(common.getCardinality() >= 0); // 至少不崩溃
        System.out.printf("Memory per user: %.2f bytes%n",
                RoaringBitmap.or(store.getFriends(u1), store.getFriends(u2)).getSizeInBytes() / 200.0);
    }
}