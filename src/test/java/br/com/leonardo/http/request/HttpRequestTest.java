package br.com.leonardo.http.request;

import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class HttpRequestTest {

    private HttpRequest<String> originalRequest;
    private RequestLine requestLine;
    private HeaderMap headers;
    private PathVariableMap pathVariables;
    private QueryParameterMap queryParameters;

    @BeforeEach
    void setUp() {
        requestLine = new RequestLine(HttpMethod.GET, "/users?name=leo", "HTTP/1.1");
        headers = new HeaderMap(Collections.emptyMap());
        pathVariables = new PathVariableMap(Collections.emptyMap());
        queryParameters = new QueryParameterMap(Collections.emptyMap());
        originalRequest = new HttpRequest<>(requestLine, headers, "initial body", pathVariables, queryParameters);
    }

    @Test
    void shouldReturnUriWithoutQueryParameters() {
        // When
        String uri = originalRequest.uri();

        // Then
        Assertions.assertThat(uri).isEqualTo("/users");
    }

    @Test
    void shouldReturnUriWhenNoQueryParametersExist() {
        // Given
        RequestLine requestLineNoQuery = new RequestLine(HttpMethod.GET, "/users", "HTTP/1.1");
        HttpRequest<String> requestNoQuery = new HttpRequest<>(requestLineNoQuery, headers, "body", pathVariables, queryParameters);

        // When
        String uri = requestNoQuery.uri();

        // Then
        Assertions.assertThat(uri).isEqualTo("/users");
    }

    @Test
    void shouldCreateNewRequestWithUpdatedBody() {
        // Given
        String newBody = "updated body";

        // When
        HttpRequest<String> updatedRequest = originalRequest.withBody(newBody);

        // Then
        Assertions.assertThat(updatedRequest).isNotNull();
        Assertions.assertThat(updatedRequest.body()).isEqualTo(newBody);
        Assertions.assertThat(updatedRequest.requestLine()).isSameAs(originalRequest.requestLine());
        Assertions.assertThat(updatedRequest.headers()).isSameAs(originalRequest.headers());
        Assertions.assertThat(updatedRequest.pathVariables()).isSameAs(originalRequest.pathVariables());
        Assertions.assertThat(updatedRequest.queryParameters()).isSameAs(originalRequest.queryParameters());
    }
}
