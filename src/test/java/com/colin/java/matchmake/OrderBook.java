package com.colin.java.matchmake;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class OrderBook {
    // 买单队列：价格从高到低排序，价格相同则时间早的优先
    PriorityQueue<Order> buyOrders;

    // 卖单队列：价格从低到高排序，价格相同则时间早的优先
    PriorityQueue<Order> sellOrders;
    
    // 策略映射
    private final Map<OrderType, MatchingStrategy> strategyMap;
    
    /**
     * 构造函数，初始化订单簿和策略映射
     */

    public OrderBook() {
        // 初始化买单堆 (大顶堆 + 时间FIFO)
        this.buyOrders = new PriorityQueue<>((o1, o2) -> {
            int priceCompare = o2.price.compareTo(o1.price); // 降序
            return priceCompare != 0 ? priceCompare : Long.compare(o1.timestamp, o2.timestamp);
        });

        // 初始化卖单堆 (小顶堆 + 时间FIFO)
        this.sellOrders = new PriorityQueue<>((o1, o2) -> {
            int priceCompare = o1.price.compareTo(o2.price); // 升序
            return priceCompare != 0 ? priceCompare : Long.compare(o1.timestamp, o2.timestamp);
        });
        
        // 初始化策略映射
        this.strategyMap = new HashMap<>();
        strategyMap.put(OrderType.EFF, new ExactFullFOKStrategy());
        strategyMap.put(OrderType.MLI, new MinLotIOCStrategy());
        strategyMap.put(OrderType.PFG, new PartialFillGTCStrategy());
        strategyMap.put(OrderType.PRM, new ProRataMinDisplayStrategy());
        strategyMap.put(OrderType.AMC, new AuctionMatchCrossStrategy());
        strategyMap.put(OrderType.OLO, new OddLotOverflowStrategy());
    }

    /**
     * 处理新订单（核心撮合逻辑）
     */
    public void processOrder(Order incomingOrder) {
        System.out.println(">>> 收到新订单: " + incomingOrder);
        
        // 根据订单类型获取对应的撮合策略
        MatchingStrategy strategy = strategyMap.get(incomingOrder.orderType);
        
        if (strategy != null) {
            // 使用策略执行撮合
            strategy.match(incomingOrder, this);
        } else {
            // 如果没有找到策略，使用默认的撮合逻辑
            System.out.println("未找到对应的撮合策略，使用默认逻辑");
            if (incomingOrder.side == Side.BUY) {
                match(incomingOrder, sellOrders, buyOrders);
            } else {
                match(incomingOrder, buyOrders, sellOrders);
            }
        }
    }

    /**
     * 撮合逻辑
     * @param incomingOrder  新进来的订单
     * @param matchQueue     对手方队列（如果是买单，这里就是卖单队列）
     * @param restingQueue   己方队列（如果没有完全成交，剩余部分放入这里）
     */
    private void match(Order incomingOrder, PriorityQueue<Order> matchQueue, PriorityQueue<Order> restingQueue) {

        // 当对手方队列不为空，且新订单还有剩余数量时，尝试撮合
        while (!matchQueue.isEmpty() && incomingOrder.quantity > 0) {
            Order bestMatch = matchQueue.peek();

            // 检查价格是否匹配
            // 如果是买单(incoming)，必须 BuyPrice >= SellPrice(bestMatch)
            // 如果是卖单(incoming)，必须 SellPrice <= BuyPrice(bestMatch)
            if (!canMatch(incomingOrder.side, incomingOrder.price, bestMatch.price)) {
                break; // 价格无法匹配，停止撮合
            }

            // 计算成交数量（取两者最小值）
            long tradeQty = Math.min(incomingOrder.quantity, bestMatch.quantity);

            // 执行交易
            System.out.printf("=== 成交! 价格: %.2f, 数量: %d (Maker: %d, Taker: %d)%n",
                    bestMatch.price, tradeQty, bestMatch.id, incomingOrder.id);

            // 更新订单数量
            incomingOrder.quantity -= tradeQty;
            bestMatch.quantity -= tradeQty;

            // 如果对手方订单已全部成交，从队列中移除
            if (bestMatch.quantity == 0) {
                matchQueue.poll();
            }
        }

        // 如果新订单还有剩余数量，将其放入己方队列（挂单 Maker）
        if (incomingOrder.quantity > 0) {
            restingQueue.add(incomingOrder);
            System.out.println("    订单未完全成交，剩余 " + incomingOrder.quantity + " 挂入订单簿.");
        }
    }

    // 辅助方法：判断价格是否满足撮合条件
    private boolean canMatch(Side incomingSide, BigDecimal incomingPrice, BigDecimal restingPrice) {
        if (incomingSide == Side.BUY) {
            // 买单价格 >= 卖单价格
            return incomingPrice.compareTo(restingPrice) >= 0;
        } else {
            // 卖单价格 <= 买单价格
            return incomingPrice.compareTo(restingPrice) <= 0;
        }
    }

    // 打印当前盘口深度
    public void printDepth() {
        System.out.println("\n--- 当前订单簿深度 ---");
        System.out.println("卖单 (Asks): " + sellOrders);
        System.out.println("买单 (Bids): " + buyOrders);
        System.out.println("--------------------\n");
    }
}