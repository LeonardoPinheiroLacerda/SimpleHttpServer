package br.com.leonardo.http.request;

import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;

import java.util.Map;

public record HttpRequest<I>(
        RequestLine requestLine,
        HeaderMap headers,
        I body,
        PathVariableMap pathVariables,
        QueryParameterMap queryParameters,
        Map<String, Object> middlewareProperties
) {
    public String uri() {
        return requestLine.uri().split("\\?")[0];
    }

    public void addMiddlewareProperty(String key, Object value) {
        this.middlewareProperties.put(key, value);
    }

    public <T> T getMiddlewareProperty(String key, Class<T> clazz) {
        return clazz.cast(this.middlewareProperties.get(key));
    }

    public void removeMiddlewareProperty(String key) {
        this.middlewareProperties.remove(key);
    }

    public void clearMiddlewareProperties() {
        this.middlewareProperties.clear();
    }

    public boolean hasMiddlewareProperty(String key) {
        return this.middlewareProperties.containsKey(key);
    }

}
