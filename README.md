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

## Detailed Examples by Package

### Collection Framework (`com.colin.java.collection`)
Examples demonstrating Java's collection framework:
- `HashMapLongChainTest`: Demonstrates hash collision effects on HashMap performance
- `HashMapSynchronizedComparisonTest`: Compares thread safety of HashMap vs. Collections.synchronizedMap
- `ConnectionTest`: Tests for collection connections and relationships

### Concurrent Programming (`com.colin.java.concurrent`)
Examples showcasing concurrency concepts:
- `ExecutorFactoryTest`: Demonstrates thread pool configuration, keep-alive time settings, and rejection policies
- `SingletonTest`: Tests singleton implementation in concurrent environments

### Thread Management (`com.colin.java.thread`)
Thread-related examples:
- `MyMainClass`: Demonstrates thread creation and execution
- `YieldTest`: Shows thread yielding behavior
- `TestLock`: Demonstrates thread locking mechanisms

### Class Loading (`com.colin.java.classloader`)
Examples exploring Java's class loading system:
- `ClassLoaderTest`: Demonstrates class loader hierarchy, parent delegation model, and loading different types of classes
- `ClassA`: Simple class for class loader testing

### Inheritance and Polymorphism (`com.colin.java.inheritance`)
Examples demonstrating OOP concepts:
- `TestConstructor`: Tests constructor behavior in inheritance hierarchies
- `TestInheritance`: Explores interface implementation and naming conflicts

### Extension Mechanisms (`com.colin.java.extension`)
- `ClassExtendTest`: Demonstrates static method binding rules with polymorphic references

### Stream API and Lambdas (`com.colin.java.stream`)
- `LambdaTest`: Comprehensive examples of Lambda expressions, comparing with anonymous classes and showing simplified syntax

### File Operations (`com.colin.java.file`)
- `TestReader`: Demonstrates file reading operations
- `TestFileReader`: Shows buffered file reading techniques
- `TestStudentIDReader`: Examples of reading specific data formats from files

### Object Behavior (`com.colin.java.object`)
- `HashcodeTest`: Tests hashcode implementation behavior

### Testing Techniques (`com.colin.java.testing`)
- `Test`: Contains simple algorithm implementations and testing approaches

## Contributing

Contributions to improve or expand the examples are welcome. Please ensure that any additions:

1. Follow the existing code style
2. Include appropriate tests
3. Add clear comments explaining the concepts demonstrated
4. Are placed in the appropriate package based on topic

## License

This project is open-source and available for educational purposes.

## Acknowledgments

This collection of examples is designed to complement Java learning resources and provide practical insights into Java programming concepts. Each example is crafted to demonstrate specific language features and programming techniques in a clear and concise manner.