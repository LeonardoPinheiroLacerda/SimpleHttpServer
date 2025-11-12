package br.com.leonardo.router.core;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.middleware.Middleware;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Type;

@ExtendWith(MockitoExtension.class)
class HttpEndpointTest {

    private HttpEndpoint<Void, String> httpEndpoint;
    private HttpEndpoint httpEndpointWithoutType;

    @Mock
    private Middleware middleware;

    @Mock
    private Middleware nextMiddleware;

    @BeforeEach
    void setUp() {
        httpEndpoint = new HttpEndpoint<>() {
            @Override
            protected HttpResponse<String> handle(HttpRequest<Void> request) throws HttpException {
                return HttpResponse
                        .<String> builder()
                        .body("Hello World")
                        .statusCode(HttpStatusCode.OK)
                        .build();
            }
        };

        httpEndpointWithoutType = new HttpEndpoint() {
            @Override
            protected HttpResponse handle(HttpRequest request) throws HttpException {
                return null;
            }
        };
    }

    @Test
    void shouldResolveInputType() {
        //Given
        Type expectedType = Void.class;

        //When
        final Type type = httpEndpoint.resolveInputType();

        //Then
        Assertions
                .assertThat(type)
                .isEqualTo(expectedType);

    }

    @Test
    void shouldResolveOutputType() {
        //Given
        Type expectedType = String.class;

        //When
        final Type type = httpEndpoint.resolveOutputType();

        //Then
        Assertions
                .assertThat(type)
                .isEqualTo(expectedType);
    }

    @Test
    void shouldResolveInputTypeToObject() {
        //Given
        Type expectedType = Object.class;

        //When
        final Type type = httpEndpointWithoutType.resolveInputType();

        //Then
        Assertions
                .assertThat(type)
                .isEqualTo(expectedType);

    }

    @Test
    void shouldResolveOutputTypeToObjecg() {
        //Given
        Type expectedType = Object.class;

        //When
        final Type type = httpEndpointWithoutType.resolveOutputType();

        //Then
        Assertions
                .assertThat(type)
                .isEqualTo(expectedType);
    }

    @Test
    void shouldAddMiddleware() {

        //Given
        int expectedSize = 1;

        //When
        httpEndpoint.addMiddleware(middleware);

        //Then
        Assertions
                .assertThat(httpEndpoint.getMiddlewares())
                .hasSize(expectedSize);

    }

    @Test
    void shouldAddTwoMiddlewares() {

        //Given
        int expectedSize = 2;

        //When
        httpEndpoint.addMiddleware(middleware);
        httpEndpoint.addMiddleware(nextMiddleware);

        //Then
        Assertions
                .assertThat(httpEndpoint.getMiddlewares())
                .hasSize(expectedSize);

    }
}