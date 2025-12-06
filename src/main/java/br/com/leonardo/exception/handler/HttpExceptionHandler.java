package br.com.leonardo.exception.handler;

import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.http.response.HttpResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class HttpExceptionHandler<T extends Throwable, O> {

    public abstract HttpResponse<O> handle(ProblemDetails problemDetails, T exception);

    public Class<? extends Throwable> resolveThrowbleType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            return (Class<? extends Throwable>) parameterizedType.getActualTypeArguments()[0]; // <T>
        }
        return Throwable.class;
    }



}
