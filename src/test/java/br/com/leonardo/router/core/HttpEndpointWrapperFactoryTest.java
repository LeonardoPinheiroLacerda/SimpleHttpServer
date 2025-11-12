package br.com.leonardo.router.core;

import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
class HttpEndpointWrapperFactoryTest {

    @Mock
    private HttpEndpoint<Void, String> httpEndpoint;

    @Test
    void shouldCreateHttpEndpointWrapper() {

        //Given
        HttpRequestData requestData = new HttpRequestData(
                new RequestLine(HttpMethod.GET, "/users", "HTTP/1.1"),
                new HashSet<>(),
                new byte[4096]
        );

        Mockito
                .when(httpEndpoint.getUri())
                .thenReturn("/users");

        //When
        final HttpEndpointWrapper<?, ?> httpEndpointWrapper = HttpEndpointWrapperFactory
                .create(httpEndpoint, requestData);

        //Then
        Assertions
                .assertThat(httpEndpointWrapper)
                .extracting("endpoint", "body")
                .containsExactly(
                        httpEndpoint,
                        new byte[4096]
                );


    }

}