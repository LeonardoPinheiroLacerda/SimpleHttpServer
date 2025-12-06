package br.com.leonardo.exception.handler.impl;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.exception.handler.model.StandardError;
import br.com.leonardo.http.response.HttpResponse;

public class HttpHttpExceptionHandler extends HttpExceptionHandler<HttpException, StandardError> {

    @Override
    public HttpResponse<StandardError> handle(ProblemDetails problemDetails, HttpException exception) {
        return HttpResponse
                .<StandardError> builder()
                .statusCode(exception.getStatusCode())
                .body(new StandardError(
                        exception.getMessage(),
                        problemDetails.getTraceId(),
                        exception.getStatusCode().getCode(),
                        problemDetails.getUri()
                ))
                .build();
    }

}
