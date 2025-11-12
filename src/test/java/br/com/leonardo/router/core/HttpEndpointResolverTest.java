package br.com.leonardo.router.core;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class HttpEndpointResolverTest {

    @Mock
    private HttpEndpoint<Void, String> httpEndpoint;

    @InjectMocks
    private HttpEndpointResolver underTest;

    @Test
    void shouldAddEndpoint() {

        //When
        Mockito
                .when(httpEndpoint.getMethod())
                .thenReturn(HttpMethod.GET);

        underTest.add(httpEndpoint);

        //Then
        Assertions.assertThat(underTest)
                .isNotNull();

    }

    @Test
    void shouldGetEndpoint() {

        //Given
        final String uri = "/users";
        final HttpMethod method = HttpMethod.GET;

        final RequestLine requestLine = new RequestLine(method, uri, "HTTP/1.1");
        final HashSet<HttpHeader> headers = new HashSet<>();
        final byte[] body = new byte[4096];

        final HttpRequestData httpRequestData = new HttpRequestData(requestLine, headers, body);

        //When
        Mockito
                .when(httpEndpoint.getMethod())
                .thenReturn(HttpMethod.GET);

        Mockito
                .when(httpEndpoint.getUri())
                .thenReturn(uri);

        underTest.add(httpEndpoint);

        final Optional<HttpEndpoint<?, ?>> httpEndpoint1 = underTest.get(httpRequestData);

        //Then
        Assertions.assertThat(httpEndpoint1)
                .isPresent()
                .isEqualTo(Optional.of(httpEndpoint));

    }

}