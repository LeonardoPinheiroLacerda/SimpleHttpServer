package br.com.leonardo.router.extractor;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.router.core.HttpEndpoint;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PathVariableExtractorTest {

    @Mock
    private HttpEndpoint<String, String> httpEndpoint;

    @Test
    void shouldExtractPathVariables() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users/1",
                "HTTP/1.1"
        );

        Mockito
                .when(httpEndpoint.getUri())
                .thenReturn("/users/{id}");

        //When
        final PathVariableMap extract = PathVariableExtractor.extract(requestLine, httpEndpoint);

        //Then
        Assertions
                .assertThat(extract)
                .extracting(p -> p.getString("id")).isEqualTo("1");

    }

    @Test
    void shouldNotExtractPathVariablesBecauseNotMatch() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users/1/test",
                "HTTP/1.1"
        );

        Mockito
                .when(httpEndpoint.getUri())
                .thenReturn("/users/{id}/teste");

        //When + Then
        Assertions.assertThatThrownBy(() -> PathVariableExtractor.extract(requestLine, httpEndpoint))
                .isInstanceOf(HttpException.class)
                .hasMessage("Path variable mismatch");

    }

}