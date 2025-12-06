package br.com.leonardo.exception.handler;

import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.http.response.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpExceptionHandlerTest {

    // Helper classes for testing
    private static class MyRuntimeExceptionHandler extends HttpExceptionHandler<RuntimeException, String> {
        @Override
        public HttpResponse<String> handle(ProblemDetails problemDetails, RuntimeException exception) {
            return null;
        }
    }

    private static class MySpecificExceptionHandler extends HttpExceptionHandler<IllegalArgumentException, String> {
        @Override
        public HttpResponse<String> handle(ProblemDetails problemDetails, IllegalArgumentException exception) {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    private static class RawTypeHandler extends HttpExceptionHandler {
        @Override
        public HttpResponse handle(ProblemDetails problemDetails, Throwable exception) {
            return null;
        }
    }

    @Test
    void shouldResolveCorrectThrowableType_whenClassIsDirectSubclass() {
        // Given
        HttpExceptionHandler<RuntimeException, String> handler = new MyRuntimeExceptionHandler();

        // When
        Class<?> throwableType = handler.resolveThrowbleType();

        // Then
        assertThat(throwableType).isEqualTo(RuntimeException.class);
    }

    @Test
    void shouldResolveCorrectThrowableType_forMoreSpecificException() {
        // Given
        HttpExceptionHandler<IllegalArgumentException, String> handler = new MySpecificExceptionHandler();

        // When
        Class<?> throwableType = handler.resolveThrowbleType();

        // Then
        assertThat(throwableType).isEqualTo(IllegalArgumentException.class);
    }

    @Test
    void shouldReturnThrowable_whenClassIsNotDirectlyParameterized() {
        // Given
        @SuppressWarnings("rawtypes")
        HttpExceptionHandler handler = new RawTypeHandler();

        // When
        Class<?> throwableType = handler.resolveThrowbleType();

        // Then
        assertThat(throwableType).isEqualTo(Throwable.class);
    }
}
