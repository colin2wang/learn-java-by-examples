package com.colin.java.stream;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试类：展示Java 8接口默认方法的各种用法和特性
 * 默认方法允许在接口中定义带有实现的方法，解决接口演化问题
 */
public class DefaultMethod {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMethod.class);

    /**
     * 基础公式接口，包含抽象方法和默认方法
     */
    interface Formula {
        /**
         * 抽象方法，需要实现类提供具体计算逻辑
         */
        double calculate(int a);

        /**
         * 默认方法，提供平方根计算功能
         * 默认方法可以被实现类继承或覆盖
         */
        default double sqrt(int a) {
            logger.debug("Calculating square root of {}", a);
            return Math.sqrt(a);
        }
    }
    
    /**
     * 扩展公式接口，继承自Formula并添加新的默认方法
     */
    interface ExtendedFormula extends Formula {
        /**
         * 新增默认方法，提供平方计算功能
         */
        default double square(int a) {
            logger.debug("Calculating square of {}", a);
            return a * a;
        }
    }
    
    /**
     * 另一个接口，定义了相同签名的默认方法，用于测试默认方法冲突
     */
    interface AnotherFormula {
        /**
         * 与Formula接口中定义了相同签名的默认方法
         */
        default double sqrt(int a) {
            logger.debug("Calculating square root from AnotherFormula for {}", a);
            return Math.pow(a, 0.5); // 功能相同但实现方式略有不同
        }
    }
    
    /**
     * 测试基本的默认方法使用 - 通过匿名内部类
     */
    @Test
    public void testDefaultMethodWithAnonymousClass() {
        logger.info("Testing default method with anonymous class");
        
        // 使用匿名内部类实现Formula接口
        Formula formula = new Formula() {
            @Override
            public double calculate(int a) {
                logger.debug("Calculating formula for input: {}", a);
                return sqrt(a * 100);
            }
        };
        
        double result1 = formula.calculate(100);
        double result2 = formula.sqrt(16);
        
        logger.info("calculate(100) result: {}", result1);
        logger.info("sqrt(16) result: {}", result2);
        
        // 验证结果
        assertEquals(100.0, result1, 0.001, "calculate(100) should return 100.0");
        assertEquals(4.0, result2, 0.001, "sqrt(16) should return 4.0");
    }
    
    /**
     * 测试使用Lambda表达式实现函数式接口
     */
    @Test
    public void testDefaultMethodWithLambda() {
        logger.info("Testing default method with lambda expression");
        
        // 使用Lambda表达式实现Formula接口的抽象方法
        Formula formula = a -> {
            logger.debug("Calculating formula with lambda for input: {}", a);
            return Math.sqrt(a * 100);
        };
        
        double result = formula.calculate(25);
        logger.info("calculate(25) with lambda result: {}", result);
        
        assertEquals(50.0, result, 0.001, "calculate(25) with lambda should return 50.0");
        
        // 默认方法仍然可以正常调用
        double sqrtResult = formula.sqrt(25);
        assertEquals(5.0, sqrtResult, 0.001, "sqrt(25) should return 5.0");
    }
    
    /**
     * 测试实现类覆盖默认方法
     */
    @Test
    public void testOverridingDefaultMethod() {
        logger.info("Testing overriding of default method");
        
        Formula formula = new Formula() {
            @Override
            public double calculate(int a) {
                return sqrt(a);
            }
            
            // 覆盖默认方法
            @Override
            public double sqrt(int a) {
                logger.debug("Using overridden sqrt method for: {}", a);
                // 提供不同的实现，例如使用Math.pow代替Math.sqrt
                return Math.pow(a, 0.5);
            }
        };
        
        double result = formula.sqrt(25);
        logger.info("Result of overridden sqrt(25): {}", result);
        
        assertEquals(5.0, result, 0.001, "Overridden sqrt(25) should return 5.0");
    }
    
    /**
     * 测试扩展接口中的默认方法
     */
    @Test
    public void testExtendedInterfaceDefaultMethod() {
        logger.info("Testing extended interface default method");
        
        ExtendedFormula formula = a -> {
            // 可以同时使用Formula和ExtendedFormula中的默认方法
            return a * a * Math.sqrt(a);
        };
        
        double result = formula.calculate(4);
        logger.info("Result of calculate(4) with extended formula: {}", result);
        
        // 计算过程: square(4) * sqrt(4) = 16 * 2 = 32
        assertEquals(32.0, result, 0.001, "Extended formula calculate(4) should return 32.0");
        
        // 单独测试新添加的默认方法
        double squareResult = formula.square(5);
        assertEquals(25.0, squareResult, 0.001, "square(5) should return 25.0");
    }
    
    /**
     * 测试解决默认方法冲突 - 当一个类实现两个具有相同签名默认方法的接口时
     */
    @Test
    public void testDefaultMethodConflictResolution() {
        logger.info("Testing default method conflict resolution");
        
        // 创建一个实现两个接口的匿名类，并显式解决默认方法冲突
        Formula formula = new Formula() {
            @Override
            public double calculate(int a) {
                return sqrt(a);
            }
        };
        
        // 定义一个内部类来演示冲突解决
        class ConflictResolver implements Formula, AnotherFormula {
            @Override
            public double calculate(int a) {
                return sqrt(a);
            }
            
            // 显式覆盖冲突的默认方法来解决二义性
            @Override
            public double sqrt(int a) {
                logger.debug("Using conflict resolution for sqrt({})", a);
                // 可以选择调用其中一个父接口的默认方法
                return Formula.super.sqrt(a);
                // 或者返回AnotherFormula.super.sqrt(a);
                // 或者提供全新的实现
            }
        }
        
        ConflictResolver resolver = new ConflictResolver();
        double result = resolver.sqrt(36);
        logger.info("Result of conflict resolved sqrt(36): {}", result);
        
        assertEquals(6.0, result, 0.001, "Conflict resolved sqrt(36) should return 6.0");
    }
    
    /**
     * 测试在实现类中通过接口名.super调用特定接口的默认方法
     */
    @Test
    public void testCallingSpecificInterfaceDefaultMethod() {
        logger.info("Testing calling specific interface default method using Interface.super");
        
        class MethodSelector implements Formula, AnotherFormula {
            @Override
            public double calculate(int a) {
                return 0; // 未使用
            }

            @Override
            public double sqrt(int a) {
                return Formula.super.sqrt(a);
            }

            // 提供两种方法分别调用不同接口的默认方法
            public double callFormulaSqrt(int a) {
                return Formula.super.sqrt(a);
            }
            
            public double callAnotherFormulaSqrt(int a) {
                return AnotherFormula.super.sqrt(a);
            }
        }
        
        MethodSelector selector = new MethodSelector();
        double result1 = selector.callFormulaSqrt(49);
        double result2 = selector.callAnotherFormulaSqrt(49);
        
        logger.info("Result from Formula.super.sqrt(49): {}", result1);
        logger.info("Result from AnotherFormula.super.sqrt(49): {}", result2);
        
        // 尽管实现不同，但结果应该相同
        assertEquals(7.0, result1, 0.001, "Formula.super.sqrt(49) should return 7.0");
        assertEquals(7.0, result2, 0.001, "AnotherFormula.super.sqrt(49) should return 7.0");
    }
}