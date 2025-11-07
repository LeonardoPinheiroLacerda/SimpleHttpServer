package br.com.leonardo.http;

public record RequestLine (
        HttpMethod method,
        String uri,
        String version
) {
}
