package br.com.leonardo.exception.handler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HttpExceptionHandlerResolver {

    private final Map<Class<? extends Throwable>, HttpExceptionHandler<?, ?>> exceptionMap = new ConcurrentHashMap<>();

    public void add(HttpExceptionHandler<?, ?> httpExceptionHandler) {
        exceptionMap.put(
                httpExceptionHandler.resolveThrowbleType(),
                httpExceptionHandler
        );
    }

    @SuppressWarnings("unchecked")
    public Optional<HttpExceptionHandler<?, ?>> get(Class<? extends Throwable> exceptionClass, boolean recursiveLookup) {
        final Optional<HttpExceptionHandler<?, ?>> httpExceptionHandler = Optional.ofNullable(exceptionMap.get(exceptionClass));

        if(httpExceptionHandler.isEmpty() && exceptionClass.getSuperclass() != Throwable.class && recursiveLookup) {
            return get((Class<? extends Throwable>) exceptionClass.getSuperclass(), true);
        }

        return httpExceptionHandler;
    }

}
