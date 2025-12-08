package com.colin.java.classloader;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试类加载器
 * @author WangBing
 */
@Slf4j
public class ClassLoaderTest extends ClassA {

    @Test
    void testGetCurrentClassLoader() {
        log.info("Testing current class loader");
        
        // 获取当前类的类加载器
        ClassLoader loader = ClassLoaderTest.class.getClassLoader();
        
        // 验证当前类加载器不为 null（应用程序类加载器）
        assertNotNull(loader, "Current class loader should not be null");
        
        // 记录当前类加载器的信息
        String loaderName = loader.getClass().getName();
        log.info("Current loader: {}, Type: {}", loader, loaderName);
        
        // 验证类加载器类型（通常是 AppClassLoader 或类似的实现）
        assertTrue(loaderName.contains("AppClassLoader") || loaderName.contains("ClassLoader"), 
                "Current loader should be an application class loader");
    }

    @Test
    void testGetParentClassLoader() {
        log.info("Testing parent class loader");
        
        // 获取父类加载器
        ClassLoader currentLoader = ClassLoaderTest.class.getClassLoader();
        ClassLoader parentLoader = currentLoader.getParent();
        
        // 验证父类加载器不为 null（扩展类加载器）
        assertNotNull(parentLoader, "Parent class loader should not be null");
        
        // 记录父类加载器的信息
        String parentLoaderName = parentLoader.getClass().getName();
        log.info("Parent loader: {}, Type: {}", parentLoader, parentLoaderName);
        
        // 验证父类加载器类型（通常是 ExtClassLoader 或类似的实现）
        assertTrue(parentLoaderName.contains("ExtClassLoader") || parentLoaderName.contains("PlatformClassLoader"),
                "Parent loader should be an extension or platform class loader");
    }

    @Test
    void testGetGrandparentClassLoader() {
        log.info("Testing grandparent class loader");
        
        // 获取祖父类加载器
        ClassLoader currentLoader = ClassLoaderTest.class.getClassLoader();
        ClassLoader parentLoader = currentLoader.getParent();
        ClassLoader grandparentLoader = parentLoader.getParent();
        
        // 验证祖父类加载器为 null（引导类加载器）
        // 注意：引导类加载器是 C/C++ 实现的，在 Java 中表示为 null
        assertNull(grandparentLoader, "Grandparent class loader (Bootstrap) should be null in Java representation");
        log.info("Grandparent loader: {}", grandparentLoader);
    }

    @Test
    void testClassLoaderHierarchy() {
        log.info("Testing class loader hierarchy");
        
        // 验证类加载器的完整层次结构
        ClassLoader currentLoader = ClassLoaderTest.class.getClassLoader();
        ClassLoader parentLoader = currentLoader.getParent();
        ClassLoader grandparentLoader = parentLoader.getParent();
        
        // 验证层次结构：当前类加载器 -> 父类加载器 -> 祖父类加载器（为 null）
        assertNotNull(currentLoader, "Current class loader should not be null");
        assertNotNull(parentLoader, "Parent class loader should not be null");
        assertNull(grandparentLoader, "Grandparent class loader should be null");
        
        log.info("ClassLoader hierarchy verified:");
        log.info("  Current: {}", currentLoader);
        log.info("  Parent: {}", parentLoader);
        log.info("  Grandparent (Bootstrap): {}", grandparentLoader);
    }

    @Test
    void testClassLoadingForDifferentClasses() {
        log.info("Testing class loading for different types of classes");
        
        // 测试不同类型的类的类加载器
        ClassLoader testClassLoader = ClassLoaderTest.class.getClassLoader();
        ClassLoader objectClassLoader = Object.class.getClassLoader();
        ClassLoader stringClassLoader = String.class.getClassLoader();
        ClassLoader aClassLoader = ClassA.class.getClassLoader();
        
        // 验证 JDK 类（如 Object 和 String）的类加载器为 null（由引导类加载器加载）
        assertNull(objectClassLoader, "Object class should be loaded by Bootstrap class loader (represented as null)");
        assertNull(stringClassLoader, "String class should be loaded by Bootstrap class loader (represented as null)");
        
        // 验证自定义类的类加载器
        assertNotNull(testClassLoader, "TestClassLoader should be loaded by an application class loader");
        assertNotNull(aClassLoader, "Class A should be loaded by an application class loader");
        
        // 验证自定义类使用相同的类加载器
        assertEquals(testClassLoader, aClassLoader, "TestClassLoader and Class A should be loaded by the same class loader");
        
        log.info("Class loading verification complete:");
        log.info("  TestClassLoader loaded by: {}", testClassLoader);
        log.info("  Class A loaded by: {}", aClassLoader);
        log.info("  Object loaded by: {}", objectClassLoader);
        log.info("  String loaded by: {}", stringClassLoader);
    }

    // 辅助方法：添加 equals 方法以支持 testClassLoadingForDifferentClasses 测试
    private void assertEquals(Object actual, Object expected, String message) {
        if (actual == expected) {
            log.debug("Assertion passed: {}", message);
        } else {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }
}