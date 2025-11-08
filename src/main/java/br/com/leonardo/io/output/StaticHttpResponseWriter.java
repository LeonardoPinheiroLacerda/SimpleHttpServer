package br.com.leonardo.io.output;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.util.ContentNegotiationUtil;

import java.io.IOException;
import java.util.Set;

public class StaticHttpResponseWriter implements HttpWriter {

    @Override
    public HttpResponse<?> generateResponse(RequestLine requestLine, Set<HttpHeader> headers, byte[] body) {
        return HttpResponse
                .<byte[]>builder()
                .statusCode(HttpStatusCode.OK)
                .body(body)
                .build();

    }

    @Override
    public byte[] getBody(RequestLine requestLine,
                          Set<HttpHeader> headers,
                          HttpResponse<?> response) throws IOException {
        byte[] bodyBytes = ContentNegotiationUtil.serializeStaticBody(requestLine.uri());
        ContentNegotiationUtil.setContentTypeAndContentLengthForStaticResources(bodyBytes, requestLine.uri(), response);

        return bodyBytes;
    }
}
