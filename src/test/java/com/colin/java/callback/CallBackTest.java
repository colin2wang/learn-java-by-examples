package com.colin.java.callback;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 回调模式测试类
 * 演示Java中回调机制的实现和使用
 * @author WangBing
 */

public class CallBackTest {
    private static final Logger logger = LoggerFactory.getLogger(CallBackTest.class);
    
    /**
     * 测试基本的回调机制
     * 验证回调方法是否被正确调用
     */
    @Test
    public void testBasicCallback() {
        logger.info("开始测试基本回调功能");
        new CallbackUser().func(new CallbackHolder());
        logger.info("基本回调功能测试完成");
    }
    
    /**
     * 测试使用匿名内部类实现回调
     */
    @Test
    public void testCallbackWithAnonymousClass() {
        logger.info("开始测试匿名内部类实现回调");
        ICallbackUser user = new CallbackUser();
        user.func(new ICallbackHolder() {
            @Override
            public void callback() {
                logger.info(">>> 匿名内部类callback()");
            }
        });
        logger.info("匿名内部类回调测试完成");
    }
    
    /**
     * 测试使用Lambda表达式实现回调
     */
    @Test
    public void testCallbackWithLambda() {
        logger.info("开始测试Lambda表达式实现回调");
        ICallbackUser user = new CallbackUser();
        user.func(() -> logger.info(">>> Lambda表达式callback()"));
        logger.info("Lambda表达式回调测试完成");
    }
}