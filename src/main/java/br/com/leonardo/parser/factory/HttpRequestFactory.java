package br.com.leonardo.parser.factory;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.parser.RequestBodyParser;
import br.com.leonardo.parser.RequestHeaderParser;
import br.com.leonardo.parser.RequestLineParser;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class HttpRequestFactory {

    private HttpRequestFactory() {}

    public static HttpRequestData fromRawRequest(String clientRawRequest) {
        RequestLine requestLine = RequestLineParser.parseRequestLine(clientRawRequest);
        Set<HttpHeader> headers = RequestHeaderParser.parseRequestHeaders(clientRawRequest);
        byte[] body = RequestBodyParser.parseRequestBody(clientRawRequest);

        log.info("Incoming request: {}", requestLine);
        log.trace("Headers parsed: size={}", headers.size());
        log.trace("Body length: {} bytes", body.length);

        return new HttpRequestData(requestLine, headers, body);
    }

}
