
package br.com.leonardo.io.output;

import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StaticHttpResponseWriterTest {

    @Mock
    private HttpRequestData requestData;

    @Mock
    private RequestLine requestLine;

    @Test
    void shouldGenerateResponse() {
        //Given
        StaticHttpResponseWriter writer = new StaticHttpResponseWriter();
        byte[] expectedBody = "test content".getBytes();

        Mockito
                .when(requestData.body())
                .thenReturn(expectedBody);

        //When
        HttpResponse<?> result = writer.generateResponse(requestData);

        //Then
        Assertions
                .assertThat(result)
                .isNotNull()
                .satisfies(response -> {
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
                    Assertions.assertThat(response.getBody()).isEqualTo(expectedBody);
                });
    }

    @Test
    void shouldGenerateResponseWithEmptyBody() {
        //Given
        StaticHttpResponseWriter writer = new StaticHttpResponseWriter();
        byte[] expectedBody = new byte[0];

        Mockito
                .when(requestData.body())
                .thenReturn(expectedBody);

        //When
        HttpResponse<?> result = writer.generateResponse(requestData);

        //Then
        Assertions
                .assertThat(result)
                .isNotNull()
                .satisfies(response -> {
                    Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.OK);
                    Assertions.assertThat(response.getBody()).isEqualTo(expectedBody);
                });
    }
}