package com.colin.java.object;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 测试对象相等性比较
 * 主要验证Integer自动装箱和常量池行为
 * @author WangBing
 */
public class EqualsTest {
    private static final Logger logger = LoggerFactory.getLogger(EqualsTest.class);

    /**
     * 自定义测试类，正确实现了equals和hashCode方法
     */
    class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age && Objects.equals(name, person.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    /**
     * 自定义测试类，只重写了equals方法，没有重写hashCode方法
     */
    class PersonNoHashCode {
        private String name;
        private int age;

        public PersonNoHashCode(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PersonNoHashCode person = (PersonNoHashCode) o;
            return age == person.age && Objects.equals(name, person.name);
        }

        @Override
        public String toString() {
            return "PersonNoHashCode{name='" + name + "', age=" + age + "}";
        }
    }

    /**
     * 测试Integer常量池中的对象相等性
     * 验证在-128到127范围内的Integer对象使用==比较时返回true
     */
    @Test
    public void testIntegerConstantPool() {
        logger.info("开始测试Integer常量池行为");

        // 在Integer常量池范围内的值（-128到127）
        Integer i = 3;
        Integer j = 3;

        logger.info("值为3的Integer对象i: {}", i);
        logger.info("值为3的Integer对象j: {}", j);
        logger.info("使用==比较的结果: {}", (i == j));

        // 验证使用==比较返回true，因为这些对象来自Integer常量池
        assertTrue(i == j, "在-128到127范围内的相同整数值的Integer对象使用==比较应返回true");

        // 验证使用equals方法也返回true（内容相等）
        assertTrue(i.equals(j), "相同值的Integer对象使用equals方法比较应返回true");

        logger.info("Integer常量池测试完成");
    }

    /**
     * 测试超出Integer常量池范围的对象相等性
     * 验证超出-128到127范围的Integer对象使用==比较时返回false
     */
    @Test
    public void testIntegerOutsideConstantPool() {
        logger.info("开始测试超出Integer常量池范围的值");

        // 超出Integer常量池范围的值（大于127）
        Integer i = 128;
        Integer j = 128;

        logger.info("值为128的Integer对象i: {}", i);
        logger.info("值为128的Integer对象j: {}", j);
        logger.info("使用==比较的结果: {}", (i == j));

        // 验证使用==比较返回false，因为这些是不同的对象实例
        assertFalse(i == j, "超出-128到127范围的相同整数值的Integer对象使用==比较应返回false");

        // 验证使用equals方法仍然返回true（内容相等）
        assertTrue(i.equals(j), "相同值的Integer对象使用equals方法比较应始终返回true");

        logger.info("超出Integer常量池范围测试完成");
    }

    /**
     * 测试Integer.valueOf()方法的行为
     * 验证valueOf()方法在常量池范围内返回缓存对象
     */
    @Test
    public void testIntegerValueOfBehavior() {
        logger.info("开始测试Integer.valueOf()方法行为");

        // 使用valueOf方法获取Integer对象
        Integer i1 = Integer.valueOf(3);
        Integer i2 = Integer.valueOf(3);
        Integer i3 = Integer.valueOf(128);
        Integer i4 = Integer.valueOf(128);

        logger.info("Integer.valueOf(3) i1: {}", i1);
        logger.info("Integer.valueOf(3) i2: {}", i2);
        logger.info("Integer.valueOf(128) i3: {}", i3);
        logger.info("Integer.valueOf(128) i4: {}", i4);

        // 验证常量池范围内的对象引用相同
        assertTrue(i1 == i2, "在常量池范围内的valueOf()返回相同对象引用");

        // 验证常量池范围外的对象引用不同
        assertFalse(i3 == i4, "在常量池范围外的valueOf()返回不同对象引用");

        // 但所有相同值的对象使用equals都应相等
        assertTrue(i1.equals(i2), "valueOf(3)返回的对象内容应相等");
        assertTrue(i3.equals(i4), "valueOf(128)返回的对象内容应相等");

        logger.info("Integer.valueOf()方法行为测试完成");
    }

    /**
     * 测试new Integer与自动装箱的区别
     * 验证new Integer始终创建新对象
     */
    @Test
    public void testNewIntegerVsAutoboxing() {
        logger.info("开始测试new Integer与自动装箱的区别");

        // 使用new Integer创建对象
        Integer newInt1 = Integer.valueOf(3);
        Integer newInt2 = Integer.valueOf(3);

        // 使用自动装箱
        Integer autoBox1 = 3;
        Integer autoBox2 = 3;

        logger.info("new Integer(3) newInt1: {}", newInt1);
        logger.info("new Integer(3) newInt2: {}", newInt2);
        logger.info("自动装箱 autoBox1: {}", autoBox1);
        logger.info("自动装箱 autoBox2: {}", autoBox2);

        // 验证自动装箱在常量池范围内返回相同对象
        assertTrue(autoBox1 == autoBox2, "自动装箱在常量池范围内应返回相同对象");

        // 所有相同值的对象使用equals都应相等
        assertTrue(newInt1.equals(newInt2), "new Integer(3)创建的对象内容应相等");
        assertTrue(newInt1.equals(autoBox1), "new Integer(3)与自动装箱3的对象内容应相等");

        logger.info("new Integer与自动装箱的区别测试完成");
    }

    /**
     * 测试equals和hashCode方法的一致性
     * 验证如果两个对象equals返回true，它们的hashCode必须相等
     */
    @Test
    public void testEqualsAndHashCodeConsistency() {
        logger.info("开始测试equals和hashCode一致性");

        // 创建两个相等的Person对象
        Person person1 = new Person("张三", 25);
        Person person2 = new Person("张三", 25);

        logger.info("person1: {}, hashCode: {}", person1, person1.hashCode());
        logger.info("person2: {}, hashCode: {}", person2, person2.hashCode());

        // 验证两个对象equals返回true
        assertTrue(person1.equals(person2), "属性相同的Person对象equals应返回true");

        // 验证两个对象的hashCode相等
        assertEquals(person1.hashCode(), person2.hashCode(),
            "equals返回true的两个对象，其hashCode必须相等");

        logger.info("equals和hashCode一致性测试完成");
    }

    /**
     * 测试只重写equals方法而不重写hashCode方法的问题
     * 验证这会导致在基于hash的集合中出现预期外的行为
     */
    @Test
    public void testOnlyEqualsWithoutHashCode() {
        logger.info("开始测试只重写equals而不重写hashCode的问题");

        // 创建两个相等的PersonNoHashCode对象
        PersonNoHashCode person1 = new PersonNoHashCode("李四", 30);
        PersonNoHashCode person2 = new PersonNoHashCode("李四", 30);

        logger.info("person1: {}, hashCode: {}", person1, person1.hashCode());
        logger.info("person2: {}, hashCode: {}", person2, person2.hashCode());

        // 验证两个对象equals返回true
        assertTrue(person1.equals(person2), "属性相同的PersonNoHashCode对象equals应返回true");

        // 注意：这里不应该断言hashCode相等，因为没有重写hashCode方法
        // 实际上，它们的hashCode很可能不同，这就是问题所在

        // 创建HashSet并添加这两个对象
        HashSet<PersonNoHashCode> set = new HashSet<>();
        boolean added1 = set.add(person1);
        boolean added2 = set.add(person2);

        logger.info("person1添加结果: {}, person2添加结果: {}", added1, added2);
        logger.info("HashSet大小: {}", set.size());

        // 问题：虽然两个对象equals返回true，但HashSet中会存在两个对象
        // 这是因为它们的hashCode不同，HashSet会将它们视为不同的对象
        assertFalse(set.size() == 1,
            "没有重写hashCode时，即使equals返回true，HashSet也会视为不同对象");

        logger.info("只重写equals而不重写hashCode的问题测试完成");
    }

    /**
     * 测试正确实现equals和hashCode方法在HashSet中的行为
     * 验证相同内容的对象在HashSet中只会存在一个
     */
    @Test
    public void testEqualsHashCodeInHashSet() {
        logger.info("开始测试正确实现equals和hashCode在HashSet中的行为");

        // 创建两个相等的Person对象
        Person person1 = new Person("王五", 35);
        Person person2 = new Person("王五", 35);

        // 创建HashSet并添加这两个对象
        HashSet<Person> set = new HashSet<>();
        boolean added1 = set.add(person1);
        boolean added2 = set.add(person2);

        logger.info("person1添加结果: {}, person2添加结果: {}", added1, added2);
        logger.info("HashSet大小: {}", set.size());

        // 验证第一个对象添加成功
        assertTrue(added1, "第一个对象应添加成功");

        // 验证第二个对象添加失败（因为它与第一个对象equals返回true且hashCode相同）
        assertFalse(added2, "第二个相等对象应添加失败");

        // 验证HashSet大小为1
        assertEquals(1, set.size(), "HashSet中应只有一个相等的对象");

        logger.info("正确实现equals和hashCode在HashSet中的行为测试完成");
    }

    /**
     * 测试正确实现equals和hashCode方法在HashMap中的行为
     * 验证相同内容的键在HashMap中只会存在一个
     */
    @Test
    public void testEqualsHashCodeInHashMap() {
        logger.info("开始测试正确实现equals和hashCode在HashMap中的行为");

        // 创建两个相等的Person对象作为键
        Person key1 = new Person("赵六", 40);
        Person key2 = new Person("赵六", 40);

        // 创建HashMap并添加键值对
        Map<Person, String> map = new HashMap<>();
        map.put(key1, "值1");
        map.put(key2, "值2");

        logger.info("HashMap大小: {}", map.size());
        logger.info("通过key1获取值: {}", map.get(key1));
        logger.info("通过key2获取值: {}", map.get(key2));

        // 验证HashMap大小为1
        assertEquals(1, map.size(), "HashMap中应只有一个相等的键");

        // 验证可以通过任意一个相等的键获取到值
        assertEquals("值2", map.get(key1), "应能通过key1获取到更新后的值");
        assertEquals("值2", map.get(key2), "应能通过key2获取到值");

        logger.info("正确实现equals和hashCode在HashMap中的行为测试完成");
    }

    /**
     * 测试String类的equals和hashCode方法
     * 验证String类正确实现了这两个方法
     */
    @Test
    public void testStringEqualsHashCode() {
        logger.info("开始测试String类的equals和hashCode方法");

        // 创建内容相同的字符串
        String str1 = new String("hello");
        String str2 = new String("hello");
        String str3 = "hello"; // 字符串常量

        logger.info("str1: {}, hashCode: {}", str1, str1.hashCode());
        logger.info("str2: {}, hashCode: {}", str2, str2.hashCode());
        logger.info("str3: {}, hashCode: {}", str3, str3.hashCode());

        // 验证所有equals比较都返回true
        assertTrue(str1.equals(str2), "内容相同的String对象equals应返回true");
        assertTrue(str1.equals(str3), "new String创建的对象与常量池中的对象equals应返回true");

        // 验证所有hashCode都相等
        assertEquals(str1.hashCode(), str2.hashCode(),
            "内容相同的String对象hashCode应相等");
        assertEquals(str1.hashCode(), str3.hashCode(),
            "new String创建的对象与常量池中的对象hashCode应相等");

        // 注意：str1和str2是不同的对象实例，但str3可能与它们是同一个实例（取决于JVM实现）
        logger.info("str1 == str2: {}", (str1 == str2));
        logger.info("str1 == str3: {}", (str1 == str3));

        logger.info("String类的equals和hashCode方法测试完成");
    }
}