package br.com.leonardo.exception;

import br.com.leonardo.enums.HttpStatusCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class HttpExceptionTest {

    @Test
    void shouldConstructHttpExceptionWithMessageStatusCodeAndPath() {
        // Given
        String message = "Test Message";
        HttpStatusCode statusCode = HttpStatusCode.BAD_REQUEST;
        String path = "/test";

        // When
        HttpException exception = new HttpException(message, statusCode, path);

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

    @Test
    void shouldConstructHttpExceptionWithMessageStatusCodePathAndCause() {
        // Given
        String message = "Test Message with Cause";
        HttpStatusCode statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;
        String path = "/error";
        Throwable cause = new RuntimeException("Original Cause");

        // When
        HttpException exception = new HttpException(message, statusCode, path, cause);

        // Then
        Assertions.assertThat(exception)
                .isNotNull()
                .hasMessage(message)
                .hasCause(cause);
        Assertions.assertThat(exception.getMessage()).isEqualTo(message);
        Assertions.assertThat(exception.getStatusCode()).isEqualTo(statusCode);
        Assertions.assertThat(exception.getPath()).isEqualTo(path);
        Assertions.assertThat(exception.getTimestamp()).isPositive();
        Assertions.assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldReturnCorrectResponseBodyWithNonNullPath() {
        // Given
        String message = "Error occurred";
        HttpStatusCode statusCode = HttpStatusCode.NOT_FOUND;
        String path = "/nonexistent";
        HttpException exception = new HttpException(message, statusCode, path);

        // When
        Map<String, Object> responseBody = exception.responseBody();

        // Then
        Assertions.assertThat(responseBody)
                .isNotNull()
                .containsEntry("message", message)
                .containsEntry("status", statusCode.getCode())
                .containsEntry("path", path);
    }

    @Test
    void shouldReturnCorrectResponseBodyWithNullPath() {
        // Given
        String message = "Error occurred";
        HttpStatusCode statusCode = HttpStatusCode.NOT_FOUND;
        String path = null;
        HttpException exception = new HttpException(message, statusCode, path);

        // When
        Map<String, Object> responseBody = exception.responseBody();

        // Then
        Assertions.assertThat(responseBody)
                .isNotNull()
                .containsEntry("message", message)
                .containsEntry("status", statusCode.getCode())
                .doesNotContainKey("path"); // Path should not be present if null
    }
}
