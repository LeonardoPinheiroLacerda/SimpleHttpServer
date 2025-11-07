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
        PathVariableMap pathVariables
) {

    public String uri() {
        return requestLine.uri().split("\\?")[0];
    }

    public Set<QueryParameter> queries() {
        final String[] chunks = requestLine.uri().split("\\?", 2);

        if(chunks.length == 1 || chunks[1].isBlank()) {
            return new HashSet<>();
        }

        final String[] queryChunks = chunks[1].split("&");

        return Stream.of(queryChunks)
                .filter(q -> q != null && !q.isBlank())
                .map(q -> {
                    final String[] parameterChunks = q.split("=");
                    if (parameterChunks.length != 2) {
                        throw new HttpException("Malformed query parameter: '" + q + "'", HttpStatusCode.BAD_REQUEST, null);
                    }
                    final String key = URLDecoder.decode(parameterChunks[0], StandardCharsets.UTF_8);
                    final String value = URLDecoder.decode(parameterChunks[1], StandardCharsets.UTF_8);
                    if (key.isBlank()) {
                        throw new HttpException("Query parameter name must not be blank", HttpStatusCode.BAD_REQUEST, null);
                    }
                    return new QueryParameter(key, value);
                })
                .collect(Collectors.toSet());
    }
    
    public Optional<QueryParameter> query(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Parameter name must not be null or blank");
        }
        return queries().stream()
                .filter(query -> query.name().equals(name))
                .findFirst();
    }

}
