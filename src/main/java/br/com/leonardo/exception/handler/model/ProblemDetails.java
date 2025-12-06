package br.com.leonardo.exception.handler.model;

import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ProblemDetails {
    private final HttpRequest<?> request;
    private final String traceId;

    public String getTraceId() {
        return this.traceId;
    }

    public String getUri() {
        return this.request.uri();
    }

    public String getMethod() {
        return this.request.method().name();
    }

    public HeaderMap getHeaders() {
        return this.request.headers();
    }

    public PathVariableMap getPathVariables() {
        return this.request.pathVariables();
    }

    public QueryParameterMap getQueryParameters() {
        return this.request.queryParameters();
    }

    public <C> Optional<C> getBodyAs(Class<C> clazz) {
        if(clazz.isInstance(this.request.body())) {
            return Optional.of(clazz.cast(this.request.body()));
        }
        return Optional.empty();
    }
}
