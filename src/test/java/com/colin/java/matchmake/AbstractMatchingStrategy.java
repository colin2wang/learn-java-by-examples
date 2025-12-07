package com.colin.java.matchmake;

import java.math.BigDecimal;

/**
 * 撮合策略抽象基类
 * 提供策略的基本功能
 */
public abstract class AbstractMatchingStrategy implements MatchingStrategy {
    private final String name;
    private final String description;
    
    public AbstractMatchingStrategy(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查价格是否匹配
     * @param buyPrice 买入价格
     * @param sellPrice 卖出价格
     * @return 是否匹配
     */
    protected boolean isPriceMatch(BigDecimal buyPrice, BigDecimal sellPrice) {
        return buyPrice.compareTo(sellPrice) >= 0;
    }
    
    /**
     * 检查价格是否严格匹配（用于EFF策略）
     * @param buyPrice 买入价格
     * @param sellPrice 卖出价格
     * @return 是否严格匹配
     */
    protected boolean isExactPriceMatch(BigDecimal buyPrice, BigDecimal sellPrice) {
        return buyPrice.compareTo(sellPrice) == 0;
    }
}