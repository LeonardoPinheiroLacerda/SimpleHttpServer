package br.com.leonardo.exception.handler.impl;

import br.com.leonardo.enums.HttpStatusCode;
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
class InternalServerErrorHttpExceptionHandlerTest {

    @Mock
    private ProblemDetails problemDetails;

    @Test
    void shouldHandleGenericExceptionAndReturnInternalServerErrorResponse() {
        // Given
        InternalServerErrorHttpExceptionHandler handler = new InternalServerErrorHttpExceptionHandler();
        String exceptionMessage = "Something unexpected happened in the service layer";
        Exception genericException = new RuntimeException(exceptionMessage);

        String traceId = "internal-error-trace-id";
        String uri = "/api/faulty";

        when(problemDetails.getTraceId()).thenReturn(traceId);
        when(problemDetails.getUri()).thenReturn(uri);

        // When
        HttpResponse<StandardError> response = handler.handle(problemDetails, genericException);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(StandardError.class);

        StandardError errorBody = response.getBody();
        assertThat(errorBody.message()).contains(exceptionMessage);
        assertThat(errorBody.traceId()).isEqualTo(traceId);
        assertThat(errorBody.status()).isEqualTo(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
        assertThat(errorBody.path()).isEqualTo(uri);
    }
}
