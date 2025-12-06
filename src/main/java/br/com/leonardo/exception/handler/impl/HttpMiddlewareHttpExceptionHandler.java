package br.com.leonardo.exception.handler.impl;

import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.exception.handler.model.StandardError;
import br.com.leonardo.http.response.HttpResponse;

public class HttpMiddlewareHttpExceptionHandler extends HttpExceptionHandler<HttpMiddlewareException, StandardError> {

    @Override
    public HttpResponse<StandardError> handle(ProblemDetails problemDetails, HttpMiddlewareException exception) {
        return HttpResponse
                .<StandardError> builder()
                .body(new StandardError(
                        "(middleware exception)" + exception.getMessage(),
                        problemDetails.getTraceId(),
                        exception.getStatusCode().getCode(),
                        problemDetails.getUri()
                ))
                .statusCode(exception.getStatusCode())
                .build();
    }

}
