package br.com.leonardo.exception.handler.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StandardErrorTest {

    @Test
    void shouldReturnCorrectValues_whenStandardErrorIsCreated() {
        // Given
        String message = "Test message";
        String traceId = "test-trace-id";
        int status = 400;
        String path = "/test/path";

        // When
        StandardError standardError = new StandardError(message, traceId, status, path);

        // Then
        assertThat(standardError.message()).isEqualTo(message);
        assertThat(standardError.traceId()).isEqualTo(traceId);
        assertThat(standardError.status()).isEqualTo(status);
        assertThat(standardError.path()).isEqualTo(path);
    }
}
