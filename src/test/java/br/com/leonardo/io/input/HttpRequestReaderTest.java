package br.com.leonardo.io.input;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.exception.HttpException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

class HttpRequestReaderTest {

    @Test
    void shouldReadRequest() throws Exception {
        try (MockedStatic<ApplicationProperties> applicationProperties = Mockito.mockStatic(ApplicationProperties.class)) {
            //Given
            String requestContent = "GET /api/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
            InputStream inputStream = new ByteArrayInputStream(requestContent.getBytes());

            applicationProperties
                    .when(ApplicationProperties::shouldLogRequests)
                    .thenReturn(false);

            //When
            String result = HttpRequestReader.readRequest(inputStream);

            //Then
            Assertions
                    .assertThat(result)
                    .isNotNull()
                    .isEqualTo(requestContent);
        }
    }

    @Test
    void shouldReadRequestWithLoggingEnabled() throws Exception {
        try (MockedStatic<ApplicationProperties> applicationProperties = Mockito.mockStatic(ApplicationProperties.class)) {
            //Given
            String requestContent = "GET /api/test HTTP/1.1\r\nHost: localhost\r\n\r\n";
            InputStream inputStream = new ByteArrayInputStream(requestContent.getBytes());

            applicationProperties
                    .when(ApplicationProperties::shouldLogRequests)
                    .thenReturn(true);

            //When
            String result = HttpRequestReader.readRequest(inputStream);

            //Then
            Assertions
                    .assertThat(result)
                    .isNotNull()
                    .isEqualTo(requestContent);
        }
    }

    @Test
    void shouldThrowHttpExceptionWhenIOExceptionOccurs() throws Exception {
        try (MockedStatic<ApplicationProperties> applicationProperties = Mockito.mockStatic(ApplicationProperties.class)) {
            //Given
            InputStream inputStream = Mockito.mock(InputStream.class);

            applicationProperties
                    .when(ApplicationProperties::shouldLogRequests)
                    .thenReturn(false);

            Mockito
                    .when(inputStream.read())
                    .thenThrow(new IOException("Read error"));

            //When & Then
            Assertions
                    .assertThatThrownBy(() -> HttpRequestReader.readRequest(inputStream))
                    .isInstanceOf(HttpException.class)
                    .hasMessageContaining("Something went wrong while your request were read");
        }
    }

}