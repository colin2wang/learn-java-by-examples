package com.colin.java.bitmap;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LoginBitmapStoreTest {

    private final LoginBitmapStore store = new LoginBitmapStore();

    /**
     * Test continuous login day counting with a bitmap-based store.
     * Principle: records 7 consecutive login dates and verifies continuousDays()
     * correctly counts them by scanning bitmap bits backward from the reference date.
     */
    @Test
    void testContinuous() {
        long uid = 12345678L;
        LocalDate today = LocalDate.of(2025, 6, 1);
        // 连续登录 7 天
        for (int i = 0; i < 7; i++) {
            store.record(uid, today.minusDays(i));
        }
        // 第 8 天未登录
        assertEquals(7, store.continuousDays(uid, today));
    }

    /**
     * Test that recording a date outside the valid range throws an exception.
     * Principle: attempts to record a date before the bitmap's supported range,
     * verifying the store rejects invalid dates with IllegalArgumentException.
     */
    @Test
    void testOutOfRange() {
        assertThrows(IllegalArgumentException.class,
                () -> store.record(1L, LocalDate.of(2019, 12, 31)));
    }

    /**
     * Test bitmap storage with a 100-million-level user ID.
     * Principle: uses RoaringBitmap's space-efficient encoding to verify that
     * large user IDs (near 100M) are stored and queried correctly.
     */
    @Test
    void testHundredMillionUser() {
        // 1 亿号段随机抽查
        long uid = 99_999_999L;
        LocalDate d = LocalDate.of(2023, 1, 1);
        store.record(uid, d);
        assertEquals(1, store.continuousDays(uid, d));
    }
}