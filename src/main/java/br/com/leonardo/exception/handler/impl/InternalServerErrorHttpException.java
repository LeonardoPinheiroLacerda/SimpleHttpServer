package br.com.leonardo.exception.handler.impl;

import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.exception.handler.model.StandardError;
import br.com.leonardo.http.response.HttpResponse;

public class InternalServerErrorHttpExceptionError extends HttpExceptionHandler<Exception, StandardError> {

    @Override
    public HttpResponse<StandardError> handle(ProblemDetails problemDetails, Exception exception) {
        return HttpResponse
                .<StandardError> builder()
                .body(new StandardError(
                        "Something went wrong (%s)".formatted(exception.getMessage()),
                        problemDetails.getTraceId(),
                        HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(),
                        problemDetails.getUri()
                ))
                .statusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
                .build();
    }

}
