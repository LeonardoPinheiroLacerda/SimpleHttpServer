package br.com.leonardo.exception;

import br.com.leonardo.http.HttpStatusCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpMiddlewareExceptionTest {

    @Test
    void shouldConstructHttpMiddlewareException() {
        // Given
        String message = "Middleware Error";
        HttpStatusCode statusCode = HttpStatusCode.FORBIDDEN;
        String path = "/secure";

        // When
        HttpMiddlewareException exception = new HttpMiddlewareException(message, statusCode, path);

        // Then
        Assertions.assertThat(exception)
                .isNotNull()
                .hasMessage(message);
        Assertions.assertThat(exception.getMessage()).isEqualTo(message);
        Assertions.assertThat(exception.getStatusCode()).isEqualTo(statusCode);
        Assertions.assertThat(exception.getPath()).isEqualTo(path);
        Assertions.assertThat(exception.getTimestamp()).isPositive();
        Assertions.assertThat(exception.getCause()).isNull();
    }
}
