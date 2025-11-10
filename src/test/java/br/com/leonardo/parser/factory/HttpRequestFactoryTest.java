package br.com.leonardo.parser.factory;

import br.com.leonardo.http.HttpMethod;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpRequestFactoryTest {

    @Test
    void shouldCreateHttpRequest() {

        //Given
        final String raw = """
                GET /users HTTP/1.1
                Host: localhost:8080
                Accept: application/json
                User-Agent: PostmanRuntime/7.26.8
                Accept-Encoding: gzip, deflate, br
                Connection: keep-alive
                Cache-Control: no-cache\r\n\r\n\r\n\r\nHELLO WORLD
                """;

        //When
        final HttpRequestData httpRequestData = HttpRequestFactory.fromRawRequest(raw);

        //Then
        Assertions
                .assertThat(httpRequestData)
                .extracting(
                        r -> r.requestLine().uri(),
                        r -> r.requestLine().method(),
                        r -> r.requestLine().version(),
                        r -> r.headers().size()
                )
                .containsExactly("/users", HttpMethod.GET, "HTTP/1.1", 6);

    }

    @Test
    void shouldCreateHttpRequestWithEmptyBody() {

        //Given
        final String raw = """
                GET /users HTTP/1.1
                Host: localhost:8080
                Accept: application/json
                User-Agent: PostmanRuntime/7.26.8
                Accept-Encoding: gzip, deflate, br
                Connection: keep-alive
                Cache-Control: no-cache\r\n\r\n\r\n\r\n
                """;

        //When
        final HttpRequestData httpRequestData = HttpRequestFactory.fromRawRequest(raw);

        //Then
        Assertions
                .assertThat(httpRequestData)
                .extracting(
                        r -> r.requestLine().uri(),
                        r -> r.requestLine().method(),
                        r -> r.requestLine().version(),
                        r -> r.headers().size()
                )
                .containsExactly("/users", HttpMethod.GET, "HTTP/1.1", 6);

    }

}