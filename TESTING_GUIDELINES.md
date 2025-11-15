# Unit Testing Guidelines for AI and Human Developers

This document provides a set of guidelines for writing unit tests in this project. The primary goal is to maintain consistency, readability, and effectiveness in our testing suite. These standards are designed to be easily understood and followed by both human developers and AI assistants.

## 1. Core Principles

- **Readability:** Tests should be as readable as a specification. A developer should understand the test's purpose without needing to read the implementation code.
- **Isolation:** Each test should be independent. The success or failure of one test must not affect another. Tests should mock external dependencies to isolate the unit of work.
- **Speed:** Unit tests should be fast. Slow tests hinder development flow and are run less frequently.
- **Reliability:** Tests must be deterministic. A test should produce the same result every time it is run. Avoid dependencies on external systems, network, or filesystem.
- **Coverage:** Strive to maximize line and branch coverage to ensure all code paths are tested.

## 2. Core Technologies

Our testing stack is composed of the following libraries. Adhere to their standard usage patterns.

- **[JUnit 5 (Jupiter)](https://junit.org/junit5/):** The primary framework for test execution, lifecycle, and assertions.
- **[Mockito](https://site.mockito.org/):** The framework for creating mock objects and verifying interactions.
- **[AssertJ](https://assertj.github.io/doc/):** The library for writing fluent and descriptive assertions.

## 3. Test Structure and Naming Conventions

### 3.1. Location

- All test classes must be located in `src/test/java`.
- The package structure of the tests must mirror the package structure of the source code being tested.
  - Example: The test for `src/main/java/com/example/MyClass.java` should be at `src/test/java/com/example/MyClassTest.java`.

### 3.2. Class Naming

- Test classes must be named after the class they test, with a `Test` suffix.
  - Example: `MyClass` -> `MyClassTest`.

### 3.3. Method Naming

- Test methods should clearly and concisely describe the scenario they are testing.
- Use the `should<ExpectedBehavior>_when<StateOrCondition>` pattern.
  - **Good:** `shouldThrowException_whenInputIsNull()`
  - **Good:** `shouldReturnAuthenticatedUser_whenCredentialsAreValid()`
  - **Avoid:** `testLogin()`, `userTest()`, `test1()`

### 3.4. Internal Test Structure (Given-When-Then)

- Structure the body of your test methods using the "Given-When-Then" pattern, separated by blank lines for clarity.

```java
// Given: Setup the test environment and preconditions.
// This includes mock definitions and object instantiation.
UserService userService = new UserService(mockUserRepository);
User user = new User("testuser");
when(mockUserRepository.findById("testuser")).thenReturn(Optional.of(user));

// When: Execute the action or method under test.
Optional<User> foundUser = userService.findUser("testuser");

// Then: Assert the expected outcome and verify interactions.
assertThat(foundUser).isPresent().contains(user);
verify(mockUserRepository).findById("testuser");
```

## 4. Writing Assertions with AssertJ

- Always use AssertJ for assertions due to its superior readability and rich set of matchers.
- Chain assertions to create a fluent, sentence-like verification.

```java
// Good
assertThat(user.getName()).isEqualTo("John Doe");
assertThat(user.getRoles()).hasSize(2).contains("ADMIN", "USER");
assertThat(resultList).isNotNull().isNotEmpty().hasSize(5);

// Avoid (using standard JUnit assertions)
// assertEquals("John Doe", user.getName());
// assertTrue(user.getRoles().size() == 2);
```

## 5. Mocking Dependencies with Mockito

- Use Mockito to create mocks for all dependencies of the class under test.
- Use `@ExtendWith(MockitoExtension.class)` at the class level to enable Mockito annotations.
- Use `@Mock` for dependencies that need to be mocked.
- Use `@InjectMocks` on the class under test to automatically inject the mocks.

### 5.1. Stubbing Method Calls

- Use `when(...).thenReturn(...)` or `when(...).thenThrow(...)` to define the behavior of mocked methods.

```java
when(mockRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "test")));
when(mockRepository.findById(2L)).thenThrow(new UserNotFoundException("User not found"));
```

### 5.2. Verifying Interactions

- Use `verify(...)` to check if a method on a mock object was called with the expected arguments.

```java
verify(mockNotifier).sendWelcomeEmail("test@example.com"); // Verify called once
verify(mockRepository, times(2)).save(any(User.class)); // Verify called twice
verify(mockLogger, never()).logError(anyString()); // Verify never called
```

## 6. Efficiency: Parameterized Tests

- When testing the same logic with different inputs and expected outputs, use JUnit 5's parameterized tests to avoid code duplication.
- Use `@ParameterizedTest` and provide a source of arguments (e.g., `@CsvSource`, `@ValueSource`, `@MethodSource`).

```java
@ParameterizedTest
@CsvSource({
    "5, 5, 10",
    "10, -5, 5",
    "0, 0, 0"
})
void shouldReturnCorrectSum_whenAddingTwoNumbers(int a, int b, int expectedSum) {
    // Given
    Calculator calculator = new Calculator();

    // When
    int result = calculator.add(a, b);

    // Then
    assertThat(result).isEqualTo(expectedSum);
}
```

## 7. Complete Example

```java
package com.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

// 1. Use MockitoExtension
@ExtendWith(MockitoExtension.class)
// 2. Class Naming Convention
class UserServiceTest {

    // 3. Mock dependencies
    @Mock
    private UserRepository mockUserRepository;

    // 4. Inject mocks into the class under test
    @InjectMocks
    private UserService userService;

    // 5. Method Naming Convention
    @Test
    void shouldReturnUser_whenUserExists() {
        // Given: Setup mocks and test data
        User expectedUser = new User(1L, "JohnDoe");
        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        // When: Call the method under test
        Optional<User> actualUser = userService.findById(1L);

        // Then: Assert the outcome and verify interactions
        assertThat(actualUser).isPresent().contains(expectedUser);
        verify(mockUserRepository).findById(1L);
    }

    @Test
    void shouldReturnEmpty_whenUserDoesNotExist() {
        // Given
        when(mockUserRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<User> actualUser = userService.findById(99L);

        // Then
        assertThat(actualUser).isNotPresent();
        verify(mockUserRepository).findById(99L);
    }
}
```
