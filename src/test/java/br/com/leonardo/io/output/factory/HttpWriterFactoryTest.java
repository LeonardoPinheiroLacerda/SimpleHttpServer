package br.com.leonardo.io.output.factory;

import br.com.leonardo.context.resolver.HttpEndpointResolver;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.io.output.ApiHttpResponseWriter;
import br.com.leonardo.io.output.HttpWriter;
import br.com.leonardo.io.output.StaticHttpResponseWriter;
import br.com.leonardo.io.output.util.ContentTypeNegotiation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpWriterFactoryTest {

    @Mock
    private ContentTypeNegotiation contentTypeNegotiation;

    @Mock
    private RequestLine requestLine;

    @Mock
    private HttpEndpointResolver resolver;

    @Test
    void shouldCreateApiHttpResponseWriter() {

        //When
        Mockito
                .when(contentTypeNegotiation.existsStatic(Mockito.anyString()))
                .thenReturn(false);

        Mockito
                .when(requestLine.uri())
                .thenReturn("/users");

        final HttpWriter httpWriter = HttpWriterFactory.create(contentTypeNegotiation, requestLine, resolver);

        //Then
        Assertions
                .assertThat(httpWriter)
                .isNotNull()
                .isInstanceOf(ApiHttpResponseWriter.class);

    }

    @Test
    void shouldCreateStaticHttpResponseWriter() {

        //When
        Mockito
                .when(contentTypeNegotiation.existsStatic(Mockito.anyString()))
                .thenReturn(true);

        Mockito
                .when(requestLine.uri())
                .thenReturn("/index.html");

        final HttpWriter httpWriter = HttpWriterFactory.create(contentTypeNegotiation, requestLine, resolver);

        //Then
        Assertions
                .assertThat(httpWriter)
                .isNotNull()
                .isInstanceOf(StaticHttpResponseWriter.class);

    }


}