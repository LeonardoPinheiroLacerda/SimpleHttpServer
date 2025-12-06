package br.com.leonardo.annotation.scanner;

import br.com.leonardo.annotation.ExceptionHandler;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.HttpExceptionHandlerResolver;
import br.com.leonardo.exception.handler.StandardHttpExceptionHandlersFactory;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.http.response.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerScannerTest {

    @InjectMocks
    private ExceptionHandlerScanner exceptionHandlerScanner;

    @Mock
    private Reflections reflections;

    // Dummy Exception Handlers for testing
    @ExceptionHandler
    public static class CustomExceptionHandler extends HttpExceptionHandler<IllegalArgumentException, String> {
        @Override
        public HttpResponse<String> handle(ProblemDetails problemDetails, IllegalArgumentException exception) {
            return null;
        }
    }
    
    @ExceptionHandler
    public static class AnotherCustomHttpExceptionHandler extends HttpExceptionHandler<HttpException, String> {
        @Override
        public HttpResponse<String> handle(ProblemDetails problemDetails, HttpException exception) {
            return null;
        }
    }

    @ExceptionHandler
    public static class DuplicateCustomExceptionHandler extends HttpExceptionHandler<IllegalArgumentException, String> {
        @Override
        public HttpResponse<String> handle(ProblemDetails problemDetails, IllegalArgumentException exception) {
            return null;
        }
    }

    @Test
    void shouldScanAndRegisterCustomHandler_whenNoConflictExists() {
        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class,
                (mock, context) -> when(mock.getTypesAnnotatedWith(ExceptionHandler.class))
                        .thenReturn(Set.of(CustomExceptionHandler.class)))) {

            try (MockedStatic<StandardHttpExceptionHandlersFactory> factory = mockStatic(StandardHttpExceptionHandlersFactory.class)) {
                factory.when(StandardHttpExceptionHandlersFactory::create).thenReturn(Collections.emptySet());

                // When
                HttpExceptionHandlerResolver resolver = exceptionHandlerScanner.scan(this.getClass());

                // Then
                assertThat(resolver.get(IllegalArgumentException.class, false)).isPresent();
            }
        }
    }

    @Test
    void shouldThrowServerInitializationException_whenDuplicateHandlerIsFound() {
        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class,
                (mock, context) -> when(mock.getTypesAnnotatedWith(ExceptionHandler.class))
                        .thenReturn(Set.of(CustomExceptionHandler.class, DuplicateCustomExceptionHandler.class)))) {
            
            try (MockedStatic<StandardHttpExceptionHandlersFactory> factory = mockStatic(StandardHttpExceptionHandlersFactory.class)) {
                factory.when(StandardHttpExceptionHandlersFactory::create).thenReturn(Collections.emptySet());

                final Class<? extends ExceptionHandlerScannerTest> clazz = this.getClass();


                // When / Then
                assertThatThrownBy(() -> exceptionHandlerScanner.scan(clazz))
                        .isInstanceOf(ServerInitializationException.class)
                        .hasMessage("There is already a handler registered for this exception type: java.lang.IllegalArgumentException");
            }
        }
    }

    @Test
    void shouldAddStandardHandlers_whenNoCustomHandlerIsPresent() {
        // Given
        HttpExceptionHandler<HttpException, ?> standardHandler = new AnotherCustomHttpExceptionHandler();

        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class,
                (mock, context) -> when(mock.getTypesAnnotatedWith(ExceptionHandler.class))
                        .thenReturn(Collections.emptySet()))) {

            try (MockedStatic<StandardHttpExceptionHandlersFactory> factory = mockStatic(StandardHttpExceptionHandlersFactory.class)) {
                factory.when(StandardHttpExceptionHandlersFactory::create).thenReturn(Set.of(standardHandler));

                // When
                HttpExceptionHandlerResolver resolver = exceptionHandlerScanner.scan(this.getClass());

                // Then
                assertThat(resolver.get(HttpException.class, false)).isPresent();
                assertThat(resolver.get(HttpException.class, false).get()).isInstanceOf(AnotherCustomHttpExceptionHandler.class);
            }
        }
    }

    @Test
    void shouldNotOverrideCustomHandler_withStandardHandler() {
        // Given
        HttpExceptionHandler<IllegalArgumentException, ?> standardHandler = new DuplicateCustomExceptionHandler(); // A standard handler for the same exception

        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class,
                (mock, context) -> when(mock.getTypesAnnotatedWith(ExceptionHandler.class))
                        .thenReturn(Set.of(CustomExceptionHandler.class)))) {

            try (MockedStatic<StandardHttpExceptionHandlersFactory> factory = mockStatic(StandardHttpExceptionHandlersFactory.class)) {
                factory.when(StandardHttpExceptionHandlersFactory::create).thenReturn(Set.of(standardHandler));

                // When
                HttpExceptionHandlerResolver resolver = exceptionHandlerScanner.scan(this.getClass());

                // Then
                assertThat(resolver.get(IllegalArgumentException.class, false)).isPresent();
                // Ensure the registered handler is the custom one, not the standard one
                assertThat(resolver.get(IllegalArgumentException.class, false).get()).isInstanceOf(CustomExceptionHandler.class);
            }
        }
    }

    // Dummy handler without a no-arg constructor
    @ExceptionHandler
    public static class HandlerWithNoDefaultConstructor extends HttpExceptionHandler<IllegalStateException, String> {
        public HandlerWithNoDefaultConstructor(String someArg) {}

        @Override
        public HttpResponse<String> handle(ProblemDetails problemDetails, IllegalStateException exception) {
            return null;
        }
    }

    @Test
    void shouldThrowServerInitializationException_whenHandlerCannotBeInstantiated() {
        // Given
        try (MockedConstruction<Reflections> mocked = mockConstruction(Reflections.class,
                (mock, context) -> when(mock.getTypesAnnotatedWith(ExceptionHandler.class))
                        .thenReturn(Set.of(HandlerWithNoDefaultConstructor.class)))) {

            try (MockedStatic<StandardHttpExceptionHandlersFactory> factory = mockStatic(StandardHttpExceptionHandlersFactory.class)) {
                factory.when(StandardHttpExceptionHandlersFactory::create).thenReturn(Collections.emptySet());


                final Class<? extends ExceptionHandlerScannerTest> clazz = this.getClass();

                // When / Then
                assertThatThrownBy(() -> exceptionHandlerScanner.scan(clazz))
                        .isInstanceOf(ServerInitializationException.class)
                        .hasMessage("It was not possible to initialize server.")
                        .hasCauseInstanceOf(NoSuchMethodException.class);
            }
        }
    }
}
