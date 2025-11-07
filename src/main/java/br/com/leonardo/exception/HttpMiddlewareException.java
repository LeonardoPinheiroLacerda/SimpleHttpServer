package br.com.leonardo.exception;

import br.com.leonardo.http.HttpStatusCode;

public class HttpMiddlewareException extends HttpException{
    public HttpMiddlewareException(String message, HttpStatusCode statusCode, String path) {
        super(message, statusCode, path);
    }
}
