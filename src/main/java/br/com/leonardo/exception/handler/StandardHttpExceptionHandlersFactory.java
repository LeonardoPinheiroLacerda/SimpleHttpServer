package br.com.leonardo.exception.handler;

import br.com.leonardo.exception.handler.impl.HttpHttpExceptionHandler;
import br.com.leonardo.exception.handler.impl.HttpMiddlewareHttpExceptionHandler;
import br.com.leonardo.exception.handler.impl.InternalServerErrorHttpExceptionHandler;

import java.util.Set;

public class StandardHttpExceptionHandlersFactory {

    private StandardHttpExceptionHandlersFactory() {
    }

    public static Set<HttpExceptionHandler<?, ?>> create() {
        return Set.of(
                new InternalServerErrorHttpExceptionHandler(),
                new HttpHttpExceptionHandler(),
                new HttpMiddlewareHttpExceptionHandler()
        );
    }
}
