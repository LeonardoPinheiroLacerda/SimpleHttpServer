package br.com.leonardo.io.output.factory;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.io.output.ApiHttpResponseWriter;
import br.com.leonardo.io.output.HttpWriter;
import br.com.leonardo.io.output.StaticHttpResponseWriter;
import br.com.leonardo.io.output.util.ContentTypeNegotiation;
import br.com.leonardo.router.core.HttpEndpointResolver;

public class HttpWriterFactory {

    private HttpWriterFactory() {}

    public static HttpWriter create(ContentTypeNegotiation contentTypeNegotiation, RequestLine requestLine, HttpEndpointResolver resolver) {
        return contentTypeNegotiation.existsStatic(requestLine.uri()) && ApplicationProperties.staticContentEnabled()
                ? new StaticHttpResponseWriter()
                : new ApiHttpResponseWriter(resolver);
    }

}
