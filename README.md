# Learn Java by Examples

This repository contains a collection of Java examples and tests designed to help developers learn and understand Java concepts through practical code samples. Each package demonstrates specific Java programming concepts with well-commented code and tests.

## Project Overview

This project serves as a comprehensive learning resource for Java programming, featuring examples across multiple domains:

- Concurrent programming and thread management
- Java collections and data structures
- Object-oriented programming concepts
- Class loading mechanisms
- Stream API and Lambda expressions
- File I/O operations
- Testing methodologies
- Design patterns

## Getting Started

### Prerequisites

- JDK 8 or higher
- Maven 3.x
- An IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Setting Up the Project

1. Clone this repository
2. Open the project in your preferred IDE
3. Ensure Maven dependencies are resolved

### Running the Tests

To run all tests, execute the following Maven command:

```bash
mvn test
```

You can also run individual test classes or methods directly from your IDE.

## Project Structure

The project follows the standard Maven directory layout with examples organized by topic:

```
src/
├── main/
│   ├── java/            # Main Java source code
│   └── resources/       # Resources (configuration files, etc.)
└── test/
    └── java/com/colin/java/  # Test classes and examples organized by topic
```

## Test Documentation

Every `@Test` method across all test files includes an English Javadoc comment explaining **what the test verifies** and **how it works** (the principle). The format is:

```java
/**
 * Test <what is being tested>.
 * Principle: <how the test works and what concept it verifies>.
 */
@Test
void testMethodName() { ... }
```

This makes it easy to understand each test's purpose without reading the implementation details.

## Detailed Examples by Package

### Algorithm (`com.colin.java.algorithm`)
- `BasicSortWithTests`: Implements and tests Bubble Sort, Insertion Sort, Selection Sort, and QuickSort with parameterized edge cases

### Bitmap (`com.colin.java.bitmap`)
- `LoginBitmapStoreTest`: Tests bitmap-based login tracking with RoaringBitmap for 100M-level user IDs
- `SnowflakeConverterTest`: Tests Snowflake ID encoding/decoding and common-friend intersection using RoaringBitmap

### Calculate (`com.colin.java.calculate`)
- `CalculateTest`: Tests probability calculation, floating-point multiplication precision, and char array operations

### Callback (`com.colin.java.callback`)
- `CallBackTest`: Demonstrates callback patterns using concrete classes, anonymous inner classes, and Lambda expressions

### Class Loading (`com.colin.java.classloader`)
- `ClassLoaderTest`: Explores class loader hierarchy (App → Extension → Bootstrap), parent delegation, and JDK vs custom class loading

### Collection Framework (`com.colin.java.collection`)
- `HashMapCollisionTest`: Demonstrates hash collision behavior and performance degradation with custom key classes
- `HashMapSynchronizedComparisonTest`: Compares thread safety of HashMap vs Collections.synchronizedMap under concurrent access
- `LRUCacheTest`: Hand-written LRU cache using HashMap + doubly-linked list
- `SimpleLRUCacheTest`: LRU cache implementation extending LinkedHashMap with accessOrder
- `MapTest`: Tests ConcurrentHashMap, HashMap, TreeMap, LinkedHashMap, HashSet, and LinkedHashSet
- `MyStackTest`: Custom multi-stack implementation using a single array with flag tagging
- `ConnectionTest`: Tests array reinitialization, List/Set conversion, and basic collection operations

### Concurrent Programming (`com.colin.java.concurrent`)
- `AlternatePrintMyLockSupportTest`: Three threads printing 1-9 in round-robin using LockSupport.park/unpark
- `AlternatePrintSemaphoreTest`: Three threads printing 1-9 using Semaphore relay chain
- `BlockingQueueTest`: Producer-consumer pattern with bounded ArrayBlockingQueue
- `BingDeadLockTest`: Deadlock demonstration and detection
- `CompletableFutureTest`: Compares Callable+Future vs CompletableFuture async patterns
- `ConcurrentHashMapTest`: Tests ConcurrentHashMap thread safety and basic CRUD operations
- `CyclicBarrierClassicTest`: CyclicBarrier for synchronized start and reusability across rounds
- `DeadlockDemoTest`: Deadlock detection using assertTimeoutPreemptively
- `LockConditionTest`: Producer-consumer using ReentrantLock + Condition variables
- `LockTest`: ReentrantLock basics, shared resource protection, and concurrent lock contention
- `MapThreadUnsafeTest`: Compares HashMap, ConcurrentHashMap, and Hashtable under concurrent put/get
- `MyLockSupportTest`: LockSupport usage patterns
- `MySemaphore`: Custom semaphore implementation
- `OrderedBlockingQueueTest`: Ordered message consumption using PriorityBlockingQueue
- `ReentryLockDeadlockTest`: Deadlock detection with ReentrantLock and timeout
- `SingleThreadTest`: Basic single-thread execution with join and timeout
- `SingletonTest`: Double-checked locking singleton, thread safety with 100 threads, and reflection attack
- `ThreadPoolBenchmarkTest`: Benchmarks sequential vs FixedThreadPool execution of 10k tasks
- `ThreadPoolExecutorTest`: Validates all 7 ThreadPoolExecutor parameters (core, max, keepAlive, factory, rejection handler)
- `ThreadSafeTest`: HashMap thread-unsafety demos: infinite loop (JDK 1.7), data loss, and data overwrite

### Extension Mechanisms (`com.colin.java.extension`)
- `ClassExtendTest`: Static method binding rules — compile-time type resolution vs runtime polymorphism

### Generics (`com.colin.java.generics`)
- `ErasureDelayedSideEffectTest`: Type erasure side effects — raw type bypass and wildcard element type preservation

### Inheritance and Polymorphism (`com.colin.java.inheritance`)
- `InterfaceTest`: Interface default method inheritance (add/sub)
- `TestConstructor`, `TestInheritance`, `TestStatic`, `MyTest`: Constructor chaining, interface conflicts, and static member behavior

### Market / OMS (`com.colin.java.market.oms`)
- `OrderBookTest`: Order book with price-time priority matching, partial fills, and multi-level sweeps
- `StockMatchingDemo`: Stock matching system demonstration
- `MatchingStrategyDemo`: Matching strategy patterns

### Market / Matching Performance (`com.colin.java.market.matching`)
- `PerformanceTest`: JIT-warmed latency benchmarks for risk manager (<10μs) and matching engine (<5μs)

### Math (`com.colin.java.math`)
- `MathTest`: Math.round() negative rounding, ^ as bitwise XOR (not power), and common Math utility functions

### Memory (`com.colin.java.memory`)
- `ConstantPoolOOMTest`: String.intern() deduplication, constant pool capacity, and literal vs intern behavior
- `ObjectReferenceTest`: SoftReference/WeakReference behavior and string constant pool interaction with weak refs

### Number (`com.colin.java.number`)
- `NumberCompareTest`: Integer cache behavior — == vs equals() for values in [-128, 127] vs outside

### Object Behavior (`com.colin.java.object`)
- `EqualsTest`: Integer constant pool, valueOf() caching, equals/hashCode contract in HashSet and HashMap
- `HashcodeTest`: String hashCode collision ("Aa" == "BB"), identity hash uniqueness, and HashSet usage

### Socket / NIO (`com.colin.java.socket`)
- `SocketTest`: NIO Selector-based non-blocking server with echo, multi-client, and graceful shutdown
- `NettyRpcTest`: Netty-based RPC client-server with method dispatch and error handling

### Stream API and Lambdas (`com.colin.java.stream`)
- `LambdaTest`: Lambda syntax progression: anonymous class → full syntax → simplified → method reference → Stream pipeline
- `FunctionalInterfaceTest`: Custom @FunctionalInterface, BiFunction, Consumer, Predicate, default/static methods
- `DefaultMethod`: Interface default methods, inheritance, override, and conflict resolution via InterfaceName.super
- `ChatCountTest`: Character frequency counting with Stream groupingBy and counting

### String (`com.colin.java.string`)
- `ConvertTest`: Character-to-number offset conversion, cipher formula (n*3+1)%26, and full string transformation pipeline

### Testing Techniques (`com.colin.java.testing`)
- `Test`: Simple algorithm implementations and testing approaches

## Contributing

Contributions to improve or expand the examples are welcome. Please ensure that any additions:

1. Follow the existing code style
2. Include appropriate tests with English Javadoc comments explaining what each `@Test` method tests and the principle behind it
3. Add clear comments explaining the concepts demonstrated
4. Are placed in the appropriate package based on topic

## License

This project is open-source and available for educational purposes.

## Acknowledgments

This collection of examples is designed to complement Java learning resources and provide practical insights into Java programming concepts. Each example is crafted to demonstrate specific language features and programming techniques in a clear and concise manner.