package com.colin.java.bitmap;

import java.util.BitSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 内存版 Bitmap 用户登录天数存储
 * 支持 1 亿用户 × 10 年（3653 天）
 */
public class LoginBitmapStore {

    private static final LocalDate BASE = LocalDate.of(2020, 1, 1);
    private static final int DAYS = (int) ChronoUnit.DAYS.between(BASE, BASE.plusYears(10)) + 1; // 3653

    /* ---------- 分片参数 ---------- */
    private static final int SHARD_BITS = 12;            // 4K shard
    private static final int SHARD_SIZE = 1 << SHARD_BITS; // 4096
    private static final int USER_PER_SHARD_BITS = 15; // 32K 用户/shard
    private static final int USER_PER_SHARD = 1 << USER_PER_SHARD_BITS; // 32768

    /* 二维数组：bank[shardIdx][userIdxInShard] */
    private final BitSet[][] bank = new BitSet[SHARD_SIZE][];

    /* ========== 对外 API ========== */

    /** 记录某天登录 */
    public void record(long uid, LocalDate date) {
        int offset = (int) ChronoUnit.DAYS.between(BASE, date);
        if (offset < 0 || offset >= DAYS) throw new IllegalArgumentException("date out of range");
        bitSet(uid).set(offset);
    }

    /** 查询连续登录天数（含当日，往前数） */
    public int continuousDays(long uid, LocalDate date) {
        int today = (int) ChronoUnit.DAYS.between(BASE, date);
        if (today < 0 || today >= DAYS) return 0;
        BitSet bs = bitSet(uid);
        int streak = 0;
        for (int i = today; i >= 0 && bs.get(i); i--) streak++;
        return streak;
    }

    /* ========== 内部 ========== */
    private BitSet bitSet(long uid) {
        int shard = (int) (uid >>> USER_PER_SHARD_BITS);
        int inner = (int) (uid & (USER_PER_SHARD - 1));
        BitSet[] arr = bank[shard];
        if (arr == null) {
            synchronized (bank) {
                arr = bank[shard];
                if (arr == null) {
                    arr = bank[shard] = new BitSet[USER_PER_SHARD];
                }
            }
        }
        BitSet bs = arr[inner];
        if (bs == null) {
            synchronized (arr) {
                bs = arr[inner];
                if (bs == null) {
                    bs = arr[inner] = new BitSet(DAYS);
                }
            }
        }
        return bs;
    }
}
