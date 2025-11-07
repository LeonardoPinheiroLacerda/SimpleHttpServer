package br.com.leonardo.http.context;

import br.com.leonardo.http.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.util.PathVariablesUtil;
import br.com.leonardo.util.QueryParametersUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpEndpointContext {

    private static final HttpEndpointContext INSTANCE = new HttpEndpointContext();

    private HttpEndpointContext() {
    }

    public static HttpEndpointContext getInstance() {
        return INSTANCE;
    }

    private final static Map<HttpMethod, Set<HttpEndpoint<?, ?>>> endpointMap = new ConcurrentHashMap<>();

    public HttpEndpointContext add(HttpEndpoint<?, ?> endpoint) {
        endpointMap
                .computeIfAbsent(endpoint.getMethod(), k -> ConcurrentHashMap.newKeySet())
                .add(endpoint);
        return this;
    }

    public Optional<HttpEndpoint<?, ?>> get(RequestLine requestLine) {
        return Optional.ofNullable(endpointMap.get(requestLine.method()))
                .flatMap(set ->
                        set
                            .stream()
                            .filter(handler ->
                                    PathVariablesUtil.match(handler.getUri(), requestLine.uri()) ||
                                    QueryParametersUtil.match(handler.getUri(), requestLine.uri())
                            )
                            .findFirst()
                );
    }

}
