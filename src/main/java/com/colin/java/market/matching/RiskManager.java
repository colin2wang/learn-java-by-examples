package com.colin.java.market.matching;

import java.util.ArrayList;
import java.util.List;

public class RiskManager {
    private List<RiskRule> rules = new ArrayList<>();

    public RiskManager() {
        // 初始化默认规则（生产环境可动态替换这些对象）
        // 1. 账户检查
        rules.add(order -> {
            // 简单模拟：买单需要有足够余额
            if (order.isBuy && (order.price * order.quantity > AccountContext.balance)) {
                return false;
            }
            return true;
        });

        // 2. 品种检查 (简单模拟白名单)
        rules.add(order -> order.productId > 0);

        // 3. 单日净额检查
        rules.add(order -> Math.abs(AccountContext.currentDailyNet + order.quantity) <= AccountContext.dailyNetLimit);
    }

    // 执行风控
    public boolean validate(SimpleOrder order) {
        // 遍历规则，Fail-fast
        for (int i = 0; i < rules.size(); i++) {
            if (!rules.get(i).check(order)) {
                return false;
            }
        }
        return true;
    }

    // 模拟热更新规则
    public void reloadRules(List<RiskRule> newRules) {
        this.rules = newRules;
    }
}