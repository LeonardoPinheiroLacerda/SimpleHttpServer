package br.com.leonardo.io.output;

import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import br.com.leonardo.router.core.HttpEndpoint;
import br.com.leonardo.router.core.HttpEndpointResolver;
import br.com.leonardo.router.core.HttpEndpointWrapper;
import br.com.leonardo.router.core.HttpEndpointWrapperFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

@Slf4j
public record ApiHttpResponseWriter (
        HttpEndpointResolver resolver
) implements HttpWriter {

    @Override
    public HttpResponse<?> generateResponse(HttpRequestData requestData) throws Exception {

        final HttpEndpoint<?, ?> httpEndpoint = resolver.get(requestData)
                .orElseThrow(() -> new HttpException("No handler were found for this endpoint",
                        HttpStatusCode.NOT_FOUND,
                        requestData.requestLine().uri())
                );

        final HttpEndpointWrapper<?, ?> httpEndpointWrapper = HttpEndpointWrapperFactory
                .create(httpEndpoint, requestData);

        httpEndpointWrapper.runMiddlewares();

        return httpEndpointWrapper.createResponse();

    }

    @Override
    public byte[] getBody(RequestLine requestLine,
                          Set<HttpHeader> headers,
                          HttpResponse<?> response) throws IOException {
        final HttpHeader acceptHeader = contentTypeNegotiation.resolveSupportedAcceptHeader(headers);

        byte[] bodyBytes = contentTypeNegotiation.serializePlainBody(response.getBody(), acceptHeader);
        contentTypeNegotiation.setContentTypeAndContentLength(acceptHeader, bodyBytes, response);

        return bodyBytes;
    }
}
