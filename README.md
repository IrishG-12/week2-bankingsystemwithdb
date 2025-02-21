# Week 1: Banking System Implementation

This project implements a basic banking system demonstrating core Java concepts including class design, OOP principles, collections, file I/O, and exception handling.

## Project Structure

```
week1-solutions/
├── src/main/java/com/banking/
│   ├── model/
│   │   ├── Account.java           # Abstract base class for accounts
│   │   ├── AccountType.java       # Enum for account types
│   │   ├── AccountFactory.java    # Factory for creating accounts
│   │   ├── SavingsAccount.java    # Savings account implementation
│   │   └── CheckingAccount.java   # Checking account implementation
│   ├── exception/
│   │   ├── BankingException.java           # Base exception class
│   │   ├── AccountNotFoundException.java    # For account lookup errors
│   │   └── InsufficientFundsException.java # For withdrawal errors
│   ├── util/
│   │   └── TransactionLogger.java  # File I/O for transaction logging
│   ├── BankingSystem.java          # Core banking operations
│   └── Main.java                   # Demo application
└── pom.xml                         # Maven configuration
```

## Features

1. Account Management
   - Abstract account base class with concrete implementations
   - Factory pattern for account creation
   - Support for different account types (Savings, Checking)
   - Balance tracking and transaction history

2. Banking Operations
   - Deposits and withdrawals
   - Monthly fee processing
   - Interest calculations for savings accounts
   - Transaction limits for checking accounts

3. Data Management
   - In-memory account storage using collections
   - O(1) account lookup using HashMap
   - Sorting and filtering capabilities
   - Transaction logging to file

4. Error Handling
   - Custom exception hierarchy
   - Proper validation and error messages
   - Safe error recovery

## Requirements

- JDK 11 or higher
- Maven 3.6+

## Building and Running

1. Build the project:
   ```bash
   mvn clean compile
   ```

2. Run the demo application:
   ```bash
   mvn exec:java -Dexec.mainClass="com.banking.Main"
   ```

3. Run the tests:
   ```bash
   # Run all tests
   mvn test

   # Run a specific test class
   mvn test -Dtest=AccountServiceTest

   # Run a specific test method
   mvn test -Dtest=AccountServiceTest#testSuccessfulTransfer
   ```

## Test Coverage

The test suite includes:

1. Account Service Tests:
   - Successful money transfer between accounts
   - Transfer with insufficient funds
   - Transfer with invalid account numbers
   - Transfer with negative amounts

2. Test Patterns Demonstrated:
   - Setup and teardown with @Before
   - Expected exception testing
   - Assertion patterns
   - Test organization (Arrange-Act-Assert)

## Key Concepts Demonstrated

1. OOP Principles
   - Inheritance (Account hierarchy)
   - Encapsulation (private fields, public methods)
   - Polymorphism (virtual method invocation)
   - Abstraction (abstract classes and methods)

2. Java Features
   - Collections Framework (List, Map)
   - Stream API
   - File I/O with NIO
   - Exception handling
   - Enums
   - Factory pattern

3. Best Practices
   - Clean code organization
   - Proper error handling
   - Documentation
   - Design patterns

## Sample Output

The demo application demonstrates:
- Account creation and management
- Transaction processing
- Balance queries
- Exception handling scenarios
- Transaction history viewing

## Testing

The implementation includes demonstrations of:
- Account creation
- Deposit/withdrawal operations
- Monthly fee processing
- Error handling
- File I/O operations

## Troubleshooting

### JDK 11 Issues

1. Java Version Check
   ```bash
   # Verify Java version
   java -version

   # Expected output should show Java 11
   # Example: openjdk version "11.0.x" ...
   ```

2. Common Issues:

   a) Multiple Java Versions
   - Problem: System has multiple Java versions installed
   - Solution:
     ```bash
     # On macOS/Linux
     export JAVA_HOME=$(/usr/libexec/java_home -v 11)

     # On Windows
     set JAVA_HOME=C:\Program Files\Java\jdk-11
     ```

   b) Maven Using Wrong Java Version
   - Problem: Maven using different Java version than system
   - Solution: Verify Maven Java version:
     ```bash
     mvn -v

     # If incorrect, set JAVA_HOME as shown above and run
     mvn clean install -U
     ```

   c) Maven Configuration Issues
   - Problem: "location of system modules is not set" warning
   - Solution: Ensure pom.xml has correct configuration:
     ```xml
     <build>
         <plugins>
             <plugin>
                 <groupId>org.apache.maven.plugins</groupId>
                 <artifactId>maven-compiler-plugin</artifactId>
                 <version>3.13.0</version>
                 <configuration>
                     <release>11</release>
                 </configuration>
             </plugin>
         </plugins>
     </build>
     ```
   - Note: Don't use source/target properties, use release instead:
     ```xml
     <!-- DON'T use these -->
     <maven.compiler.source>11</maven.compiler.source>
     <maven.compiler.target>11</maven.compiler.target>

     <!-- DO use this in build configuration -->
     <release>11</release>
     ```

   d) Dependency Issues
   - Problem: Dependencies not compatible with Java 11
   - Solution: Verify all dependencies support Java 11
   - Example compatible versions:
     ```xml
     <dependencies>
         <dependency>
             <groupId>junit</groupId>
             <artifactId>junit</artifactId>
             <version>4.13.2</version>
             <scope>test</scope>
         </dependency>
     </dependencies>
     ```

3. IDE-Specific Issues:

   a) VSCode:
   - Set Java runtime in settings.json:
     ```json
     "java.configuration.runtimes": [{
       "name": "JavaSE-11",
       "path": "/path/to/jdk-11",
       "default": true
     }]
     ```

   b) IntelliJ IDEA:
   - File → Project Structure → Project
   - Set Project SDK to Java 11
   - Set Project language level to 11

4. Build Errors:
   - Clean Maven cache: `mvn clean`
   - Update dependencies: `mvn dependency:purge-local-repository`
   - Rebuild: `mvn clean install`

## Next Steps

Future enhancements could include:
- Database persistence
- User interface
- Authentication/Authorization
- Additional account types
- Transaction categories
