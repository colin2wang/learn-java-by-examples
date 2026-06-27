package com.colin.java.callback;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 回调模式测试类
 * 演示Java中回调机制的实现和使用
 * @author WangBing
 */
@Slf4j
public class CallBackTest {
    /**
     * Test basic callback mechanism using concrete CallbackHolder implementation.
     * Principle: passes a CallbackHolder to CallbackUser.func(), which invokes
     * the holder's callback() method, demonstrating the inversion-of-control pattern.
     */
    @Test
    public void testBasicCallback() {
        log.info("开始测试基本回调功能");
        new CallbackUser().func(new CallbackHolder());
        log.info("基本回调功能测试完成");
    }
    
    /**
     * Test callback implementation using an anonymous inner class.
     * Principle: creates an inline ICallbackHolder implementation via anonymous class
     * to demonstrate pre-Java-8 callback style.
     */
    @Test
    public void testCallbackWithAnonymousClass() {
        log.info("开始测试匿名内部类实现回调");
        ICallbackUser user = new CallbackUser();
        user.func(new ICallbackHolder() {
            @Override
            public void callback() {
                log.info(">>> 匿名内部类callback()");
            }
        });
        log.info("匿名内部类回调测试完成");
    }
    
    /**
     * Test callback implementation using a Lambda expression.
     * Principle: since ICallbackHolder is a @FunctionalInterface, a Lambda () -> log.info(...)
     * can replace the anonymous class, demonstrating Java 8 functional callback style.
     */
    @Test
    public void testCallbackWithLambda() {
        log.info("开始测试Lambda表达式实现回调");
        ICallbackUser user = new CallbackUser();
        user.func(() -> log.info(">>> Lambda表达式callback()"));
        log.info("Lambda表达式回调测试完成");
    }
}