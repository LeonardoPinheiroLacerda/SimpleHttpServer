package br.com.leonardo.http.request;

import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    private HttpRequest<String> originalRequest;
    private RequestLine requestLine;
    private HeaderMap headers;
    private PathVariableMap pathVariables;
    private QueryParameterMap queryParameters;
    private Map<String, Object> middlewareProperties;

    @BeforeEach
    void setUp() {
        requestLine = new RequestLine(HttpMethod.GET, "/users?name=leo", "HTTP/1.1");
        headers = new HeaderMap(Collections.emptyMap());
        pathVariables = new PathVariableMap(Collections.emptyMap());
        queryParameters = new QueryParameterMap(Collections.emptyMap());
        middlewareProperties = new HashMap<>();
        originalRequest = new HttpRequest<>(requestLine, headers, "initial body", pathVariables, queryParameters, middlewareProperties);
    }

    @Test
    void shouldReturnUriWithoutQueryParameters() {
        // When
        String uri = originalRequest.uri();

        // Then
        assertThat(uri).isEqualTo("/users");
    }

    @Test
    void shouldReturnUriWhenNoQueryParametersExist() {
        // Given
        RequestLine requestLineNoQuery = new RequestLine(HttpMethod.GET, "/users", "HTTP/1.1");
        HttpRequest<String> requestNoQuery = new HttpRequest<>(requestLineNoQuery, headers, "body", pathVariables, queryParameters, new HashMap<>());

        // When
        String uri = requestNoQuery.uri();

        // Then
        assertThat(uri).isEqualTo("/users");
    }

    @Test
    void shouldAddMiddlewareProperty_whenCalled() {
        // Given
        String key = "testKey";
        String value = "testValue";

        // When
        originalRequest.addMiddlewareProperty(key, value);

        // Then
        assertThat(originalRequest.middlewareProperties()).containsKey(key);
        assertThat(originalRequest.middlewareProperties().get(key)).isEqualTo(value);
    }

    @Test
    void shouldGetMiddlewareProperty_whenPropertyExists() {
        // Given
        String key = "testKey";
        String value = "testValue";
        originalRequest.addMiddlewareProperty(key, value);

        // When
        String retrievedValue = originalRequest.getMiddlewareProperty(key, String.class);

        // Then
        assertThat(retrievedValue).isEqualTo(value);
    }

    @Test
    void shouldRemoveMiddlewareProperty_whenPropertyExists() {
        // Given
        String key = "testKey";
        String value = "testValue";
        originalRequest.addMiddlewareProperty(key, value);

        // When
        originalRequest.removeMiddlewareProperty(key);

        // Then
        assertThat(originalRequest.middlewareProperties()).doesNotContainKey(key);
    }

    @Test
    void shouldClearAllMiddlewareProperties_whenCalled() {
        // Given
        originalRequest.addMiddlewareProperty("key1", "value1");
        originalRequest.addMiddlewareProperty("key2", "value2");

        // When
        originalRequest.clearMiddlewareProperties();

        // Then
        assertThat(originalRequest.middlewareProperties()).isEmpty();
    }

    @Test
    void shouldReturnTrueFromHasMiddlewareProperty_whenPropertyExists() {
        // Given
        String key = "testKey";
        originalRequest.addMiddlewareProperty(key, "someValue");

        // When
        boolean hasProperty = originalRequest.hasMiddlewareProperty(key);

        // Then
        assertThat(hasProperty).isTrue();
    }

    @Test
    void shouldReturnFalseFromHasMiddlewareProperty_whenPropertyDoesNotExist() {
        // Given
        String key = "nonExistentKey";

        // When
        boolean hasProperty = originalRequest.hasMiddlewareProperty(key);

        // Then
        assertThat(hasProperty).isFalse();
    }
}
