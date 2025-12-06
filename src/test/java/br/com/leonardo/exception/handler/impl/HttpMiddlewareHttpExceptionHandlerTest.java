package br.com.leonardo.exception.handler.impl;

import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.exception.handler.model.StandardError;
import br.com.leonardo.http.response.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpMiddlewareHttpExceptionHandlerTest {

    @Mock
    private ProblemDetails problemDetails;

    @Test
    void shouldHandleHttpMiddlewareExceptionAndReturnStandardErrorResponse() {
        // Given
        HttpMiddlewareHttpExceptionHandler handler = new HttpMiddlewareHttpExceptionHandler();
        String errorMessage = "Middleware failed";
        HttpStatusCode statusCode = HttpStatusCode.FORBIDDEN;
        String path = "/api/secure";
        HttpMiddlewareException exception = new HttpMiddlewareException(errorMessage, statusCode, path);

        String traceId = "middleware-trace-id";
        String uri = "/api/secure";

        when(problemDetails.getTraceId()).thenReturn(traceId);
        when(problemDetails.getUri()).thenReturn(uri);

        // When
        HttpResponse<StandardError> response = handler.handle(problemDetails, exception);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(statusCode);
        assertThat(response.getBody()).isInstanceOf(StandardError.class);

        StandardError errorBody = response.getBody();
        assertThat(errorBody.message()).isEqualTo("(middleware exception)" + errorMessage);
        assertThat(errorBody.traceId()).isEqualTo(traceId);
        assertThat(errorBody.status()).isEqualTo(statusCode.getCode());
        assertThat(errorBody.path()).isEqualTo(path);
    }
}
