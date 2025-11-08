package br.com.leonardo.io.output.factory;

import br.com.leonardo.io.output.ApiHttpResponseWriter;
import br.com.leonardo.io.output.HttpWriter;
import br.com.leonardo.io.output.StaticHttpResponseWriter;
import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.util.ContentNegotiationUtil;

public class HttpWriterFactory {

    private HttpWriterFactory() {}

    public static HttpWriter create(RequestLine requestLine) {
        return ContentNegotiationUtil.existsStatic(requestLine.uri()) && ApplicationProperties.staticContentEnabled()
                ? new StaticHttpResponseWriter()
                : new ApiHttpResponseWriter();
    }

}
