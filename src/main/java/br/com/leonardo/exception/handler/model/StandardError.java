package br.com.leonardo.exception.handler.model;

public record StandardError(
        String message,
        String traceId,
        int status,
        String path
) {
}
