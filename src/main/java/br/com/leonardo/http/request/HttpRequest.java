package br.com.leonardo.http.request;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record HttpRequest<I>(
        RequestLine requestLine,
        Set<HttpHeader> headers,
        I body,
        PathVariableMap pathVariables,
        QueryParameterMap queryParameters
) {
    public String uri() {
        return requestLine.uri().split("\\?")[0];
    }
}
