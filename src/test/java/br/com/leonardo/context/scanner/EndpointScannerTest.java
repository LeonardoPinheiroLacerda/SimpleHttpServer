package br.com.leonardo.context.scanner;

import br.com.leonardo.context.annotations.Endpoint;
import br.com.leonardo.context.resolver.HttpEndpointResolver;
import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.router.core.HttpEndpoint;
import br.com.leonardo.router.core.middleware.Middleware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

// --- Helper classes for testing ---
class TestMiddleware extends Middleware {
    @Override
    public void run(HttpRequest<?> request) throws HttpMiddlewareException {
    }
}

abstract class AbstractTestMiddleware extends Middleware {
}

@Endpoint(url = "/simple", method = HttpMethod.GET)
class SimpleEndpoint extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) throws HttpException {
        return null;
    }
}

@Endpoint(url = "/with-middleware", method = HttpMethod.POST, middlewares = {TestMiddleware.class})
class EndpointWithMiddleware extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) throws HttpException {
        return null;
    }
}

@Endpoint(url = "/abstract", method = HttpMethod.GET)
abstract class AbstractEndpoint extends HttpEndpoint<Void, String> {
}

@Endpoint(url = "/bad-middleware", method = HttpMethod.PUT, middlewares = {AbstractTestMiddleware.class})
class EndpointWithAbstractMiddleware extends HttpEndpoint<Void, String> {
    @Override
    public HttpResponse<String> handle(HttpRequest<Void> request) throws HttpException {
        return null;
    }
}


@ExtendWith(MockitoExtension.class)
class EndpointScannerTest {

    private EndpointScanner scanner;

    @Mock
    private Reflections reflections;

    @Captor
    private ArgumentCaptor<HttpEndpoint<?, ?>> endpointCaptor;

    @BeforeEach
    void setUp() {
        scanner = new EndpointScanner();
    }

    @Test
    void shouldReturnEmptyResolver_whenNoEndpointsAreFound() {
        // Given
        when(reflections.getTypesAnnotatedWith(Endpoint.class)).thenReturn(Collections.emptySet());

        // When
        try (MockedConstruction<HttpEndpointResolver> mockedResolver = mockConstruction(HttpEndpointResolver.class)) {
            scanner.scan(reflections);

            // Then
            HttpEndpointResolver resolverInstance = mockedResolver.constructed().get(0);
            verify(resolverInstance, never()).add(any());
        }
    }

    @Test
    void shouldScanAndRegisterEndpoint_whenSimpleEndpointIsFound() {
        // Given
        when(reflections.getTypesAnnotatedWith(Endpoint.class)).thenReturn(Set.of(SimpleEndpoint.class));

        // When
        try (MockedConstruction<HttpEndpointResolver> mockedResolver = mockConstruction(HttpEndpointResolver.class)) {
            scanner.scan(reflections);

            // Then
            HttpEndpointResolver resolverInstance = mockedResolver.constructed().get(0);
            verify(resolverInstance).add(endpointCaptor.capture());
            HttpEndpoint<?, ?> capturedEndpoint = endpointCaptor.getValue();

            assertThat(capturedEndpoint.getUri()).isEqualTo("/simple");
            assertThat(capturedEndpoint.getMethod()).isEqualTo(HttpMethod.GET);
            assertThat(capturedEndpoint.getMiddlewares()).isEmpty();
        }
    }

    @Test
    void shouldScanAndRegisterEndpoint_withMiddlewares() {
        // Given
        when(reflections.getTypesAnnotatedWith(Endpoint.class)).thenReturn(Set.of(EndpointWithMiddleware.class));

        // When
        try (MockedConstruction<HttpEndpointResolver> mockedResolver = mockConstruction(HttpEndpointResolver.class)) {
            scanner.scan(reflections);

            // Then
            HttpEndpointResolver resolverInstance = mockedResolver.constructed().get(0);
            verify(resolverInstance).add(endpointCaptor.capture());
            HttpEndpoint<?, ?> capturedEndpoint = endpointCaptor.getValue();

            assertThat(capturedEndpoint.getUri()).isEqualTo("/with-middleware");
            assertThat(capturedEndpoint.getMethod()).isEqualTo(HttpMethod.POST);
            assertThat(capturedEndpoint.getMiddlewares()).hasSize(1);
            assertThat(capturedEndpoint.getMiddlewares().get(0)).isInstanceOf(TestMiddleware.class);
        }
    }

    @Test
    void shouldThrowException_whenEndpointCannotBeInstantiated() {
        // Given
        when(reflections.getTypesAnnotatedWith(Endpoint.class)).thenReturn(Set.of(AbstractEndpoint.class));

        // When & Then
        assertThatThrownBy(() -> scanner.scan(reflections))
                .isInstanceOf(ServerInitializationException.class)
                .hasMessage("It was not possible to initialize server.")
                .hasCauseInstanceOf(InstantiationException.class);
    }

    @Test
    void shouldThrowException_whenMiddlewareCannotBeInstantiated() {
        // Given
        when(reflections.getTypesAnnotatedWith(Endpoint.class)).thenReturn(Set.of(EndpointWithAbstractMiddleware.class));

        // When & Then
        assertThatThrownBy(() -> scanner.scan(reflections))
                .isInstanceOf(ServerInitializationException.class)
                .hasMessage("It was not possible to initialize server.")
                .hasCauseInstanceOf(InstantiationException.class);
    }
}