package br.com.leonardo.client.output;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.context.HttpEndpointContext;
import br.com.leonardo.http.context.HttpEndpoint;
import br.com.leonardo.http.request.PathVariableMap;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.util.ContentNegotiationUtil;
import br.com.leonardo.util.PathVariablesUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class ApiClientHttpResponseWriter implements ClientHttpWriter {

    @Override
    public HttpResponse<?> generateResponse(RequestLine requestLine, Set<HttpHeader> headers, byte[] body) throws HttpException {

        final HttpEndpoint<?, ?> endpointHandler = HttpEndpointContext
                .getInstance()
                .get(requestLine)
                .orElse(null);

        if (endpointHandler == null) {
            log.error("No endpoint handler found for request: {}", requestLine);
            throw new HttpException(
                    "No handler were found for this endpoint " +
                            (
                                    ApplicationProperties.staticContentEnabled()
                                            ? "or no static content were found"
                                            : ""
                            ),
                    HttpStatusCode.NOT_FOUND,
                    requestLine.uri()
            );
        }

        PathVariableMap pathVariableMap = PathVariablesUtil.extract(requestLine, endpointHandler);

        endpointHandler
                .runMiddlewares(requestLine, headers, body, pathVariableMap);

        try {
            return endpointHandler
                    .createResponse(requestLine, headers, body, pathVariableMap);
        }catch (IOException e) {
            throw new HttpException(e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR, requestLine.uri());
        }
    }

    @Override
    public byte[] getBody(RequestLine requestLine,
                          Set<HttpHeader> headers,
                          HttpResponse<?> response) throws IOException {
        final HttpHeader acceptHeader = ContentNegotiationUtil.resolveSupportedAcceptHeader(headers);

        byte[] bodyBytes = ContentNegotiationUtil.serializePlainBody(response.getBody(), acceptHeader);
        ContentNegotiationUtil.setContentTypeAndContentLength(acceptHeader, bodyBytes, response);

        return bodyBytes;
    }
}
