package br.com.leonardo.http.request;

import br.com.leonardo.http.RequestLine;

public record HttpRequest<I>(
        RequestLine requestLine,
        HeaderMap headers,
        I body,
        PathVariableMap pathVariables,
        QueryParameterMap queryParameters
) {
    public String uri() {
        return requestLine.uri().split("\\?")[0];
    }
}
