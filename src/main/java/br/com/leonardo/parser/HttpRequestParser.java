package br.com.leonardo.parser;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class HttpRequestParser {

    private HttpRequestParser() {}

    public static HttpRequestData parseHttpRequest(String clientRawRequest) {
        RequestLine requestLine = RequestLineParser.parseRequestLine(clientRawRequest);
        Set<HttpHeader> headers = RequestHeaderParser.parseRequestHeaders(clientRawRequest);
        byte[] body = RequestBodyParser.parseRequestBody(clientRawRequest);

        log.info("Incoming request: {}", requestLine);
        log.trace("Headers parsed: size={}", headers.size());
        log.trace("Body length: {} bytes", body == null ? 0 : body.length);

        return new HttpRequestData(requestLine, headers, body);
    }

}
