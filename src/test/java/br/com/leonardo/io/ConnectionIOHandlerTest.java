package br.com.leonardo.io;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.io.input.HttpRequestReader;
import br.com.leonardo.io.output.ApiHttpResponseWriter;
import br.com.leonardo.io.output.factory.HttpWriterFactory;
import br.com.leonardo.io.output.util.ContentTypeNegotiation;
import br.com.leonardo.router.core.HttpEndpointResolver;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@ExtendWith(MockitoExtension.class)
class ConnectionIOHandlerTest {

    @InjectMocks
    private ConnectionIOHandler underTest;

    @Mock
    private Socket clientConnection;

    @Mock
    private HttpEndpointResolver resolver;

    @Mock
    private InputStream inputStream;

    @Mock
    private OutputStream outputStream;

    @Mock
    private ApiHttpResponseWriter apiHttpResponseWriter;


    @Test
    void shouldRun() throws IOException {

        //When
        try(MockedStatic<HttpRequestReader> reader = Mockito.mockStatic(HttpRequestReader.class);
            MockedStatic<HttpWriterFactory> writer = Mockito.mockStatic(HttpWriterFactory.class);
            MockedStatic<ApplicationProperties> properties = Mockito.mockStatic(ApplicationProperties.class)
        ){
            reader
                    .when(() -> HttpRequestReader.readRequest(Mockito.any(InputStream.class)))
                    .thenReturn("GET /api/test HTTP/1.1\r\nHost: localhost\r\n\r\n");

            writer
                    .when(() -> HttpWriterFactory.create(Mockito.any(ContentTypeNegotiation.class), Mockito.any(RequestLine.class), Mockito.eq(resolver)))
                    .thenReturn(apiHttpResponseWriter);

            properties
                    .when(ApplicationProperties::shouldLogRequests)
                    .thenReturn(true);

            properties
                    .when(ApplicationProperties::shouldLogResponses)
                    .thenReturn(true);

            Mockito
                    .when(apiHttpResponseWriter.writeResponse(Mockito.any(OutputStream.class), Mockito.any(), Mockito.any(RequestLine.class), Mockito.any()))
                    .thenReturn("olha a resposta");

            Mockito
                    .when(clientConnection.getInputStream())
                    .thenReturn(inputStream);

            Mockito
                    .when(clientConnection.getOutputStream())
                    .thenReturn(outputStream);

            //Then
            Assertions
                    .assertThatNoException()
                    .isThrownBy(() -> underTest.run());
        }

    }

    @Test
    void shouldNotRunBecauseHttpException() throws IOException {

        //When
        try(MockedStatic<HttpRequestReader> reader = Mockito.mockStatic(HttpRequestReader.class);
            MockedStatic<HttpWriterFactory> writer = Mockito.mockStatic(HttpWriterFactory.class);
            MockedStatic<ApplicationProperties> properties = Mockito.mockStatic(ApplicationProperties.class)
        ){
            reader
                    .when(() -> HttpRequestReader.readRequest(Mockito.any(InputStream.class)))
                    .thenReturn("GET /api/test HTTP/1.1\r\nHost: localhost\r\n\r\n");

            writer
                    .when(() -> HttpWriterFactory.create(Mockito.any(ContentTypeNegotiation.class), Mockito.any(RequestLine.class), Mockito.eq(resolver)))
                    .thenReturn(apiHttpResponseWriter);

            properties
                    .when(ApplicationProperties::shouldLogRequests)
                    .thenReturn(true);

            properties
                    .when(ApplicationProperties::shouldLogResponses)
                    .thenReturn(true);

            Mockito
                    .when(apiHttpResponseWriter.writeResponse(Mockito.any(OutputStream.class), Mockito.any(), Mockito.any(RequestLine.class), Mockito.any()))
                    .thenThrow(new HttpException("IOException", HttpStatusCode.BAD_REQUEST, "/users"));

            Mockito
                    .when(clientConnection.getInputStream())
                    .thenReturn(inputStream);

            Mockito
                    .when(clientConnection.getOutputStream())
                    .thenReturn(outputStream);

            //Then
            Assertions
                    .assertThatNoException()
                    .isThrownBy(() -> underTest.run());
        }

    }

    @Test
    void shouldNotRunBecauseException() throws IOException {

        //When
        try(MockedStatic<HttpRequestReader> reader = Mockito.mockStatic(HttpRequestReader.class);
            MockedStatic<HttpWriterFactory> writer = Mockito.mockStatic(HttpWriterFactory.class);
            MockedStatic<ApplicationProperties> properties = Mockito.mockStatic(ApplicationProperties.class)
        ){
            reader
                    .when(() -> HttpRequestReader.readRequest(Mockito.any(InputStream.class)))
                    .thenReturn("GET /api/test HTTP/1.1\r\nHost: localhost\r\n\r\n");

            writer
                    .when(() -> HttpWriterFactory.create(Mockito.any(ContentTypeNegotiation.class), Mockito.any(RequestLine.class), Mockito.eq(resolver)))
                    .thenReturn(apiHttpResponseWriter);

            properties
                    .when(ApplicationProperties::shouldLogRequests)
                    .thenReturn(true);

            properties
                    .when(ApplicationProperties::shouldLogResponses)
                    .thenReturn(true);

            Mockito
                    .when(apiHttpResponseWriter.writeResponse(Mockito.any(OutputStream.class), Mockito.any(), Mockito.any(RequestLine.class), Mockito.any()))
                    .thenThrow(new RuntimeException("EXCEPTION"));

            Mockito
                    .when(clientConnection.getInputStream())
                    .thenReturn(inputStream);

            Mockito
                    .when(clientConnection.getOutputStream())
                    .thenReturn(outputStream);

            //Then
            Assertions
                    .assertThatNoException()
                    .isThrownBy(() -> underTest.run());
        }

    }

}