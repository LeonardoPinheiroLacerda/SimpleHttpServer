package br.com.leonardo.io.output;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.parser.factory.model.HttpRequestData;

import java.io.IOException;
import java.util.Set;

public class StaticHttpResponseWriter implements HttpWriter {

    @Override
    public HttpResponse<?> generateResponse(HttpRequestData requestData) {
        return HttpResponse
                .<byte[]>builder()
                .statusCode(HttpStatusCode.OK)
                .body(requestData.body())
                .build();

    }

    @Override
    public byte[] getBody(RequestLine requestLine,
                          Set<HttpHeader> headers,
                          HttpResponse<?> response) throws IOException {
        byte[] bodyBytes = contentTypeNegotiation.serializeStaticBody(requestLine.uri());
        contentTypeNegotiation.setContentTypeAndContentLengthForStaticResources(bodyBytes, requestLine.uri(), response);

        return bodyBytes;
    }
}
