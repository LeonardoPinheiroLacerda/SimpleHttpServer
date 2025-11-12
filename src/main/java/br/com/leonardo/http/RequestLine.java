package br.com.leonardo.http;

import br.com.leonardo.enums.HttpMethod;

public record RequestLine (
        HttpMethod method,
        String uri,
        String version
) {
}
