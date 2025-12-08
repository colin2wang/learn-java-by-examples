package com.colin.java.market.matching;

// 风控规则接口
public interface RiskRule {
    boolean check(SimpleOrder order);
}
