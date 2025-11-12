package br.com.leonardo.router.core;

import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import br.com.leonardo.router.matcher.EndpointUriMatcher;
import br.com.leonardo.router.matcher.PathVariableUriMatcher;
import br.com.leonardo.router.matcher.QueryParameterUriMatcher;
import br.com.leonardo.router.matcher.UriMatcher;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HttpEndpointResolver {

    private final Map<HttpMethod, Set<HttpEndpoint<?, ?>>> endpointMap = new ConcurrentHashMap<>();
    private final UriMatcher endpointUriMatcher = new EndpointUriMatcher(
            List.of(
                    new PathVariableUriMatcher(),
                    new QueryParameterUriMatcher()
            )
    );

    public HttpEndpointResolver add(HttpEndpoint<?, ?> endpoint) {
        endpointMap
                .computeIfAbsent(endpoint.getMethod(), k -> ConcurrentHashMap.newKeySet())
                .add(endpoint);
        return this;
    }

    public Optional<HttpEndpoint<?, ?>> get(HttpRequestData requestData) {

        final RequestLine requestLine = requestData.requestLine();

        return Optional.ofNullable(endpointMap.get(requestLine.method()))
                .flatMap(set ->
                        set
                                .stream()
                                .filter(handler ->
                                        endpointUriMatcher.match(handler.getUri(), requestLine.uri())
                                )
                                .findFirst()
                );

    }

}
