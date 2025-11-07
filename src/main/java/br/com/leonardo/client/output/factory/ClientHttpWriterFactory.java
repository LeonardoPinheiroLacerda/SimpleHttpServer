package br.com.leonardo.client.output.factory;

import br.com.leonardo.client.output.ApiClientHttpResponseWriter;
import br.com.leonardo.client.output.ClientHttpWriter;
import br.com.leonardo.client.output.StaticClientHttpResponseWriter;
import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.util.ContentNegotiationUtil;

public class ClientHttpWriterFactory {

    private ClientHttpWriterFactory() {}

    public static ClientHttpWriter create(RequestLine requestLine) {
        return ContentNegotiationUtil.existsStatic(requestLine.uri()) && ApplicationProperties.staticContentEnabled()
                ? new StaticClientHttpResponseWriter()
                : new ApiClientHttpResponseWriter();
    }

}
