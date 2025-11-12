package br.com.leonardo.http.middleware;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

class MiddlewareTest {

    private HttpRequest<?> request;

    @BeforeEach
    void setUp() {
        request = new HttpRequest<>(
                new RequestLine(HttpMethod.GET, "/test", "HTTP/1.1"),
                null, null, null, null
        );
    }

    // Concrete implementation for testing
    static class TestMiddleware extends Middleware {
        private final Runnable action;

        TestMiddleware(Runnable action) {
            this.action = action;
        }

        @Override
        public void run(HttpRequest<?> request) throws HttpMiddlewareException {
            if (action != null) {
                action.run();
            }
        }
    }

    @Test
    void shouldRunSingleMiddleware() {
        // Given
        AtomicBoolean ran = new AtomicBoolean(false);
        Middleware middleware = new TestMiddleware(() -> ran.set(true));

        // When
        middleware.handle(request);

        // Then
        Assertions.assertThat(ran.get()).isTrue();
    }

    @Test
    void shouldChainAndRunTwoMiddlewares() {
        // Given
        AtomicBoolean firstRan = new AtomicBoolean(false);
        AtomicBoolean secondRan = new AtomicBoolean(false);

        Middleware first = new TestMiddleware(() -> firstRan.set(true));
        Middleware second = new TestMiddleware(() -> secondRan.set(true));
        first.setNext(second);

        // When
        first.handle(request);

        // Then
        Assertions.assertThat(firstRan.get()).isTrue();
        Assertions.assertThat(secondRan.get()).isTrue();
    }

    @Test
    void shouldThrowHttpMiddlewareExceptionWhenHttpExceptionIsCaught() {
        // Given
        HttpException httpException = new HttpException("Forbidden", HttpStatusCode.FORBIDDEN, "/test");
        Middleware middleware = new TestMiddleware(() -> {
            throw httpException;
        });

        // When & Then
        Assertions.assertThatThrownBy(() -> middleware.handle(request))
                .isInstanceOf(HttpMiddlewareException.class)
                .hasMessage("Forbidden")
                .satisfies(ex -> {
                    HttpMiddlewareException thrown = (HttpMiddlewareException) ex;
                    Assertions.assertThat(thrown.getStatusCode()).isEqualTo(HttpStatusCode.FORBIDDEN);
                    Assertions.assertThat(thrown.getPath()).isEqualTo("/test");
                });
    }

    @Test
    void shouldThrowHttpMiddlewareExceptionWhenGenericExceptionIsCaught() {
        // Given
        Middleware middleware = new TestMiddleware(() -> {
            throw new IllegalStateException("Something went wrong");
        });

        // When & Then
        Assertions.assertThatThrownBy(() -> middleware.handle(request))
                .isInstanceOf(HttpMiddlewareException.class)
                .hasMessage("Something unexpected happened on middlewares")
                .satisfies(ex -> {
                    HttpMiddlewareException thrown = (HttpMiddlewareException) ex;
                    Assertions.assertThat(thrown.getStatusCode()).isEqualTo(HttpStatusCode.INTERNAL_SERVER_ERROR);
                    Assertions.assertThat(thrown.getPath()).isEqualTo("/test");
                });
    }

    @Test
    void shouldStopExecutionChainWhenExceptionIsThrown() {
        // Given
        AtomicBoolean secondRan = new AtomicBoolean(false);
        Middleware first = new TestMiddleware(() -> {
            throw new RuntimeException("stop here");
        });
        Middleware second = new TestMiddleware(() -> secondRan.set(true));
        first.setNext(second);

        // When
        Assertions.assertThatThrownBy(() -> first.handle(request))
                .isInstanceOf(HttpMiddlewareException.class);

        // Then
        Assertions.assertThat(secondRan.get()).isFalse();
    }

    @Test
    void shouldGetAndSetNextMiddleware() {
        // Given
        Middleware first = new TestMiddleware(null);
        Middleware second = new TestMiddleware(null);

        // When
        first.setNext(second);

        // Then
        Assertions.assertThat(first.getNext()).isSameAs(second);
    }
}
