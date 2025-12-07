package com.colin.java.finance;

import java.util.Arrays;

/**
 * 极简的高性能 OrderBook
 * 采用 Array + Cursor 方式，内存连续，CPU Cache 友好。
 */
public class ArrayOrderBook {
    // 预分配数组，避免扩容抖动。假设深度不超过1000。
    // 真实场景会使用 RingBuffer 或更复杂的数组管理。
    private final long[] prices;
    private final long[] quantities;
    private int size;
    private final boolean isAskBook; // true为卖盘（价格低优先），false为买盘（价格高优先）

    public ArrayOrderBook(int capacity, boolean isAskBook) {
        this.prices = new long[capacity];
        this.quantities = new long[capacity];
        this.size = 0;
        this.isAskBook = isAskBook;
    }

    public void add(long price, long quantity) {
        // 极简插入排序，保持数组有序
        // 生产环境通常使用二分查找确定位置，然后 System.arraycopy 移动数据
        int i = size - 1;
        if (isAskBook) {
            // 卖盘：价格升序排列 (低价在前)
            while (i >= 0 && prices[i] > price) {
                prices[i + 1] = prices[i];
                quantities[i + 1] = quantities[i];
                i--;
            }
        } else {
            // 买盘：价格降序排列 (高价在前)
            while (i >= 0 && prices[i] < price) {
                prices[i + 1] = prices[i];
                quantities[i + 1] = quantities[i];
                i--;
            }
        }
        prices[i + 1] = price;
        quantities[i + 1] = quantity;
        size++;
    }

    // 撮合逻辑 - 优化版
    public long match(long incomingPrice, long incomingQty) {
        long tradedQty = 0;

        // 直接使用索引遍历，避免额外的cursor变量
        int i = 0;
        while (i < size && incomingQty > 0) {
            long bestPrice = prices[i];

            // 检查价格是否匹配
            boolean priceMatch = isAskBook ? (incomingPrice >= bestPrice) : (incomingPrice <= bestPrice);
            if (!priceMatch) break;

            long available = quantities[i];
            long matchSize = Math.min(incomingQty, available);

            incomingQty -= matchSize;
            quantities[i] -= matchSize;
            tradedQty += matchSize;

            // 如果当前档位吃完了，直接跳过（延迟压缩）
            if (quantities[i] == 0) {
                i++; // 只是标记跳过，不立即压缩数组
            } else {
                i++; // 继续下一个价格
            }
        }

        // 注意：我们移除了实时压缩数组的代码，因为这是性能瓶颈
        // 在高性能交易系统中，通常会在非关键路径上定期压缩数组，或使用环形缓冲区实现

        return tradedQty;
    }

    public void clear() {
        size = 0;
    }
}