package br.com.leonardo.io.output;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import br.com.leonardo.router.core.HttpEndpoint;
import br.com.leonardo.router.core.HttpEndpointResolver;
import br.com.leonardo.router.core.HttpEndpointWrapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class ApiHttpResponseWriterTest {


    @InjectMocks
    private ApiHttpResponseWriter underTest;

    @Mock
    private HttpEndpointResolver resolver;

    @Mock
    private HttpRequestData requestData;

    @Mock
    private RequestLine requestLine;

    @Mock
    private HttpEndpointWrapper<?, ?> httpEndpointWrapper;

    @Mock
    private HttpEndpoint<?, ?> httpEndpoint;

    @Mock
    private OutputStream outputStream;

    @Test
    void shouldNotGenerateResponseBecauseEndpointDontExists() {

        //When
        Mockito
                .when(resolver.get(requestData))
                .thenReturn(Optional.empty());

        Mockito
                .when(requestData.requestLine())
                .thenReturn(requestLine);

        Mockito
                .when(requestLine.uri())
                .thenReturn("/users");

        //Then
        Assertions
                .assertThatThrownBy(() -> underTest.generateResponse(requestData))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void shouldGenerateResponse() {

        //When
        Mockito
                .when(httpEndpoint.getUri())
                .thenReturn("/users");

        Mockito
                .when(resolver.get(requestData))
                .thenReturn(Optional.of(httpEndpoint));

        Mockito
                .when(requestData.requestLine())
                .thenReturn(requestLine);

        Mockito
                .when(requestLine.uri())
                .thenReturn("/users");

        //Then
        Assertions
                .assertThatThrownBy(() -> underTest.generateResponse(requestData))
                .isInstanceOf(HttpException.class);

    }

    @Test
    void shouldWriteResponse() throws IOException {

        //Given
        final HttpResponse<Object> response = HttpResponse.builder()
                .<String>body("helpme")
                .statusCode(HttpStatusCode.OK)
                .build();

        final Set<HttpHeader> headers = new HashSet<>();

        //When
        final String rawReponse = underTest.writeResponse(outputStream, response, requestLine, headers);

        //Then
        Assertions
                .assertThat(rawReponse)
                .isNotNull();
    }

    @Test
    void shouldWriteResponseException() throws IOException {

        //Given
        final HttpResponse<Object> response = HttpResponse.builder()
                .<String>body("helpme")
                .statusCode(HttpStatusCode.OK)
                .build();

        final Set<HttpHeader> headers = new HashSet<>();

        final HttpException exception = new HttpException("Something went wrong", HttpStatusCode.BAD_REQUEST, "/users");

        //When
        final String rawReponse = underTest.writeResponse(outputStream, response, requestLine, headers, exception);

        //Then
        Assertions
                .assertThat(rawReponse)
                .isNotNull();
    }
}