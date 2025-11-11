package br.com.leonardo.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ServerInitializationExceptionTest {

    @Test
    void shouldConstructServerInitializationExceptionWithMessage() {
        // Given
        String message = "Server failed to initialize";

        // When
        ServerInitializationException exception = new ServerInitializationException(message);

        // Then
        Assertions.assertThat(exception)
                .isNotNull()
                .hasMessage(message);
        Assertions.assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldConstructServerInitializationExceptionWithMessageAndCause() {
        // Given
        String message = "Server failed to initialize due to network issue";
        Throwable cause = new IOException("Network is unreachable");

        // When
        ServerInitializationException exception = new ServerInitializationException(message, cause);

        // Then
        Assertions.assertThat(exception)
                .isNotNull()
                .hasMessage(message)
                .hasCause(cause);
    }
}
