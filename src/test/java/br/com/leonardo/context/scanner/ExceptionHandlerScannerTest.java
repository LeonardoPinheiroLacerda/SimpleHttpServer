package br.com.leonardo.context.scanner;

import br.com.leonardo.context.annotations.ExceptionHandler;
import br.com.leonardo.context.resolver.HttpExceptionHandlerResolver;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.StandardHttpExceptionHandlersFactory;
import br.com.leonardo.exception.handler.impl.HttpHttpExceptionHandler;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.http.response.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ExceptionHandlerScannerTest {

    @Mock
    private Reflections reflections;

    @InjectMocks
    private ExceptionHandlerScanner scanner;


    // Helper classes for testing
    public static class CustomTestException extends RuntimeException {
    }

    @ExceptionHandler
    public static class CustomTestExceptionHandler extends HttpExceptionHandler<CustomTestException, Void> {
        @Override
        public HttpResponse<Void> handle(ProblemDetails problemDetails, CustomTestException exception) {
            return null;
        }
    }

    @ExceptionHandler
    public static class AnotherCustomTestExceptionHandler extends HttpExceptionHandler<CustomTestException, Void> {
        @Override
        public HttpResponse<Void> handle(ProblemDetails problemDetails, CustomTestException exception) {
            return null;
        }
    }

    @ExceptionHandler
    public static class HttpExceptionOverrideHandler extends HttpExceptionHandler<HttpException, Void> {
        @Override
        public HttpResponse<Void> handle(ProblemDetails problemDetails, HttpException exception) {
            return null;
        }
    }

    @ExceptionHandler
    public abstract static class AbstractExceptionHandler extends HttpExceptionHandler<IllegalArgumentException, Void> {
    }


    @Test
    void shouldScanAndRegisterCustomHandler_whenFoundOnClasspath() {
        // Given
        Set<Class<?>> handlers = Set.of(CustomTestExceptionHandler.class);
        when(reflections.getTypesAnnotatedWith(ExceptionHandler.class)).thenReturn(handlers);

        // When
        HttpExceptionHandlerResolver resolver = scanner.scan(reflections);

        // Then
        Optional<HttpExceptionHandler<?, ?>> handler = resolver.get(CustomTestException.class);
        assertThat(handler).isPresent();
        assertThat(handler.get()).isInstanceOf(CustomTestExceptionHandler.class);
    }

    @Test
    void shouldAddStandardHandlers_whenNoCustomHandlersArePresent() {
        // Given
        when(reflections.getTypesAnnotatedWith(ExceptionHandler.class)).thenReturn(Collections.emptySet());

        // When
        HttpExceptionHandlerResolver resolver = scanner.scan(reflections);

        // Then
        Set<HttpExceptionHandler<?, ?>> standardHandlers = StandardHttpExceptionHandlersFactory.create();

        assertThat(standardHandlers).isNotEmpty(); // Sanity check

        for (HttpExceptionHandler<?, ?> handler : standardHandlers) {
            Class<? extends Throwable> throwableType = handler.resolveThrowbleType();
            assertThat(resolver.get(throwableType))
                    .as("Checking for handler for " + throwableType.getSimpleName())
                    .isPresent()
                    .get()
                    .isInstanceOf(handler.getClass());
        }
    }

    @Test
    void shouldNotOverrideCustomHandler_withStandardHandler() {
        // Given
        Set<Class<?>> handlers = Set.of(HttpExceptionOverrideHandler.class);
        when(reflections.getTypesAnnotatedWith(ExceptionHandler.class)).thenReturn(handlers);

        // When
        HttpExceptionHandlerResolver resolver = scanner.scan(reflections);

        // Then
        Optional<HttpExceptionHandler<?, ?>> handler = resolver.get(HttpException.class);
        assertThat(handler).isPresent();
        assertThat(handler.get()).isInstanceOf(HttpExceptionOverrideHandler.class);
        assertThat(handler.get()).isNotInstanceOf(HttpHttpExceptionHandler.class);
    }

    @Test
    void shouldThrowException_whenDuplicateHandlerIsRegistered() {
        // Given
        Set<Class<?>> handlers = Set.of(CustomTestExceptionHandler.class, AnotherCustomTestExceptionHandler.class);
        when(reflections.getTypesAnnotatedWith(ExceptionHandler.class)).thenReturn(handlers);

        // When & Then
        assertThatThrownBy(() -> scanner.scan(reflections))
                .isInstanceOf(ServerInitializationException.class)
                .hasMessage("There is already a handler registered for this exception type: " + CustomTestException.class.getName());
    }

    @Test
    void shouldThrowException_whenHandlerCannotBeInstantiated() {
        // Given
        Set<Class<?>> handlers = Set.of(AbstractExceptionHandler.class);
        when(reflections.getTypesAnnotatedWith(ExceptionHandler.class)).thenReturn(handlers);

        // When & Then
        assertThatThrownBy(() -> scanner.scan(reflections))
                .isInstanceOf(ServerInitializationException.class)
                .hasMessage("It was not possible to initialize server.")
                .cause().isInstanceOf(InstantiationException.class);
    }
}