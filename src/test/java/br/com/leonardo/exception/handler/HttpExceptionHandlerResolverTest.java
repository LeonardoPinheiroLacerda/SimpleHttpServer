package br.com.leonardo.exception.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class HttpExceptionHandlerResolverTest {

    @Mock
    private HttpExceptionHandler<IllegalArgumentException, ?> illegalArgumentHandler;
    @Mock
    private HttpExceptionHandler<RuntimeException, ?> runtimeHandler;
    @Mock
    private HttpExceptionHandler<IOException, ?> ioHandler;

    private HttpExceptionHandlerResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new HttpExceptionHandlerResolver();
    }

    @Test
    void shouldReturnHandler_whenExactMatchTypeFound() {
        // Given
        doReturn(IllegalArgumentException.class).when(illegalArgumentHandler).resolveThrowbleType();
        resolver.add(illegalArgumentHandler);

        // When
        Optional<HttpExceptionHandler<?, ?>> foundHandler = resolver.get(IllegalArgumentException.class, false);

        // Then
        assertThat(foundHandler).isPresent().contains(illegalArgumentHandler);
    }

    @Test
    void shouldReturnEmpty_whenNoMatchAndNoRecursion() {
        // Given
        doReturn(IllegalArgumentException.class).when(illegalArgumentHandler).resolveThrowbleType();
        resolver.add(illegalArgumentHandler);
        Optional<HttpExceptionHandler<?, ?>> foundHandler = resolver.get(NullPointerException.class, false);

        // Then
        assertThat(foundHandler).isNotPresent();
    }

    @Test
    void shouldReturnSuperclassHandler_whenRecursiveLookupIsEnabled() {
        // Given
        doReturn(RuntimeException.class).when(runtimeHandler).resolveThrowbleType();
        resolver.add(runtimeHandler);

        // When
        // NullPointerException extends RuntimeException
        Optional<HttpExceptionHandler<?, ?>> foundHandler = resolver.get(NullPointerException.class, true);

        // Then
        assertThat(foundHandler).isPresent().contains(runtimeHandler);
    }

    @Test
    void shouldNotReturnSubclassHandler_forSuperclassException() {
        // Given
        doReturn(IllegalArgumentException.class).when(illegalArgumentHandler).resolveThrowbleType();
        resolver.add(illegalArgumentHandler); // Handles a subclass of RuntimeException

        // When
        Optional<HttpExceptionHandler<?, ?>> foundHandler = resolver.get(RuntimeException.class, true);

        // Then
        assertThat(foundHandler).isNotPresent();
    }

    @Test
    void shouldStopAtThrowable_whenRecursiveLookupFindsNoMatch() {
        // Given
        doReturn(IOException.class).when(ioHandler).resolveThrowbleType();
        resolver.add(ioHandler); // Handles a checked exception

        // When
        Optional<HttpExceptionHandler<?, ?>> foundHandler = resolver.get(NullPointerException.class, true);

        // Then
        assertThat(foundHandler).isNotPresent();
    }

    @Test
    void shouldReturnMostSpecificHandler_whenMultipleSuperclassHandlersExist() {
        // Given
        HttpExceptionHandler<Exception, ?> exceptionHandler = mock(HttpExceptionHandler.class);
        doReturn(Exception.class).when(exceptionHandler).resolveThrowbleType();
        doReturn(RuntimeException.class).when(runtimeHandler).resolveThrowbleType();

        resolver.add(exceptionHandler);
        resolver.add(runtimeHandler); // More specific

        // When
        Optional<HttpExceptionHandler<?, ?>> foundHandler = resolver.get(NullPointerException.class, true);

        // Then
        assertThat(foundHandler).isPresent().contains(runtimeHandler);
    }
}
