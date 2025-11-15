package br.com.leonardo.router.core.middleware;

import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.http.request.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MiddlewareTest {

    // Concrete implementation for testing the abstract Middleware class
    private static class ConcreteMiddleware extends Middleware {
        private boolean runCalled = false;

        @Override
        public void run(HttpRequest<?> request) throws HttpMiddlewareException {
            this.runCalled = true;
            // Simulate some logic or exception if needed for specific tests
        }

        public boolean isRunCalled() {
            return runCalled;
        }

        public void resetRunCalled() {
            runCalled = false;
        }
    }

    private ConcreteMiddleware middleware;

    @Mock
    private HttpRequest<?> mockHttpRequest;

    @BeforeEach
    void setUp() {
        middleware = new ConcreteMiddleware();
    }

    @Test
    void shouldReturnFalse_whenNextMiddlewareIsNull() {
        // Given
        middleware.setNext(null);

        // When
        boolean hasNext = middleware.hasNext();

        // Then
        assertThat(hasNext).isFalse();
    }

    @Test
    void shouldReturnTrue_whenNextMiddlewareIsNotNull() {
        // Given
        middleware.setNext(new ConcreteMiddleware());

        // When
        boolean hasNext = middleware.hasNext();

        // Then
        assertThat(hasNext).isTrue();
    }

    @Test
    void shouldReturnTrue_whenNextMiddlewareIsNull() {
        // Given
        middleware.setNext(null);

        // When
        boolean isLast = middleware.isLast();

        // Then
        assertThat(isLast).isTrue();
    }

    @Test
    void shouldReturnFalse_whenNextMiddlewareIsNotNull() {
        // Given
        middleware.setNext(new ConcreteMiddleware());

        // When
        boolean isLast = middleware.isLast();

        // Then
        assertThat(isLast).isFalse();
    }

    @Test
    void shouldReturnFalse_whenCallingNextAndNextMiddlewareIsNull() throws HttpMiddlewareException {
        // Given
        middleware.setNext(null);

        // When
        boolean calledNext = middleware.next(mockHttpRequest);

        // Then
        assertThat(calledNext).isFalse();
        verifyNoInteractions(mockHttpRequest); // Ensure no interaction with request if no next middleware
    }

    @Test
    void shouldReturnTrueAndCallRunOnNext_whenNextMiddlewareIsNotNull() throws HttpMiddlewareException {
        // Given
        ConcreteMiddleware nextMiddleware = spy(new ConcreteMiddleware()); // Use spy to verify run method
        middleware.setNext(nextMiddleware);

        // When
        boolean calledNext = middleware.next(mockHttpRequest);

        // Then
        assertThat(calledNext).isTrue();
        verify(nextMiddleware, times(1)).run(mockHttpRequest);
    }

    @Test
    void shouldCallRunMethod_whenMiddlewareIsExecuted() throws HttpMiddlewareException {
        // Given
        middleware.resetRunCalled(); // Ensure initial state

        // When
        middleware.run(mockHttpRequest);

        // Then
        assertThat(middleware.isRunCalled()).isTrue();
    }
}
