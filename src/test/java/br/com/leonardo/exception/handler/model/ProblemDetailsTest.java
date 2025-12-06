package br.com.leonardo.exception.handler.model;

import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProblemDetailsTest {

    @Mock
    private HttpRequest<?> mockHttpRequest;
    @Mock
    private HeaderMap mockHeaderMap;
    @Mock
    private PathVariableMap mockPathVariableMap;
    @Mock
    private QueryParameterMap mockQueryParameterMap;

    private ProblemDetails problemDetails;
    private String testTraceId = "test-trace-id";
    private String testUri = "/test/uri";
    private HttpMethod testMethod = HttpMethod.GET;
    private TestBody testBody;

    @BeforeEach
    void setUp() {
        testBody = new TestBody("body-content");
        when(mockHttpRequest.uri()).thenReturn(testUri);
        when(mockHttpRequest.method()).thenReturn(testMethod);
        when(mockHttpRequest.headers()).thenReturn(mockHeaderMap);
        when(mockHttpRequest.pathVariables()).thenReturn(mockPathVariableMap);
        when(mockHttpRequest.queryParameters()).thenReturn(mockQueryParameterMap);
        when(mockHttpRequest.body()).thenReturn(testBody);

        problemDetails = new ProblemDetails(mockHttpRequest, testTraceId);
    }

    @Test
    void shouldReturnCorrectTraceId_whenGetTraceIdIsCalled() {
        // Given setup in beforeEach

        // When
        String traceId = problemDetails.getTraceId();

        // Then
        assertThat(traceId).isEqualTo(testTraceId);
    }

    @Test
    void shouldReturnCorrectUri_whenGetUriIsCalled() {
        // Given setup in beforeEach

        // When
        String uri = problemDetails.getUri();

        // Then
        assertThat(uri).isEqualTo(testUri);
    }

    @Test
    void shouldReturnCorrectMethod_whenGetMethodIsCalled() {
        // Given setup in beforeEach

        // When
        String method = problemDetails.getMethod();

        // Then
        assertThat(method).isEqualTo(testMethod.name());
    }

    @Test
    void shouldReturnCorrectHeaders_whenGetHeadersIsCalled() {
        // Given setup in beforeEach

        // When
        HeaderMap headers = problemDetails.getHeaders();

        // Then
        assertThat(headers).isEqualTo(mockHeaderMap);
    }

    @Test
    void shouldReturnCorrectPathVariables_whenGetPathVariablesIsCalled() {
        // Given setup in beforeEach

        // When
        PathVariableMap pathVariables = problemDetails.getPathVariables();

        // Then
        assertThat(pathVariables).isEqualTo(mockPathVariableMap);
    }

    @Test
    void shouldReturnCorrectQueryParameters_whenGetQueryParametersIsCalled() {
        // Given setup in beforeEach

        // When
        QueryParameterMap queryParameters = problemDetails.getQueryParameters();

        // Then
        assertThat(queryParameters).isEqualTo(mockQueryParameterMap);
    }

    @Test
    void shouldReturnBodyAsOptionalOfClass_whenBodyTypeMatches() {
        // Given setup in beforeEach

        // When
        Optional<TestBody> body = problemDetails.getBodyAs(TestBody.class);

        // Then
        assertThat(body).isPresent().contains(testBody);
    }

    @Test
    void shouldReturnEmptyOptional_whenBodyTypeDoesNotMatch() {
        // Given setup in beforeEach

        // When
        Optional<String> body = problemDetails.getBodyAs(String.class);

        // Then
        assertThat(body).isNotPresent();
    }

    // Helper class for testing getBodyAs
    private static class TestBody {
        private String content;

        public TestBody(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}
