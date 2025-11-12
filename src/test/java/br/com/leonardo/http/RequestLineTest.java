package br.com.leonardo.http;

import br.com.leonardo.enums.HttpMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void shouldCreateRequestLineAndGetValues() {
        // Given
        HttpMethod method = HttpMethod.GET;
        String uri = "/test";
        String version = "HTTP/1.1";

        // When
        RequestLine requestLine = new RequestLine(method, uri, version);

        // Then
        Assertions.assertThat(requestLine).isNotNull();
        Assertions.assertThat(requestLine.method()).isEqualTo(method);
        Assertions.assertThat(requestLine.uri()).isEqualTo(uri);
        Assertions.assertThat(requestLine.version()).isEqualTo(version);
    }
}
