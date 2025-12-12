package br.com.leonardo.context.scanner;

import br.com.leonardo.context.annotations.ExceptionHandler;
import br.com.leonardo.context.resolver.HttpExceptionHandlerResolver;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.StandardHttpExceptionHandlersFactory;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ExceptionHandlerScanner implements Scanner<HttpExceptionHandlerResolver>{

    public HttpExceptionHandlerResolver scan(Reflections reflections) {

        final HttpExceptionHandlerResolver resolver = new HttpExceptionHandlerResolver();

        final Set<Class<?>> types = reflections
                .getTypesAnnotatedWith(ExceptionHandler.class);

        for (Class<?> exceptionHandler : types) {
            try {
                final HttpExceptionHandler<?, ?> httpExceptionHandler =
                        (HttpExceptionHandler<?, ?>) exceptionHandler
                                .getDeclaredConstructor()
                                .newInstance();

                if(resolver.get(httpExceptionHandler.resolveThrowbleType()).isPresent()) {
                    log.error("There is already a handler registered for this exception type: {}", httpExceptionHandler.resolveThrowbleType().getName());
                    throw new ServerInitializationException("There is already a handler registered for this exception type: " + httpExceptionHandler.resolveThrowbleType().getName());
                }

                resolver.add(httpExceptionHandler);

            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new ServerInitializationException("It was not possible to initialize server.", e);
            }
        }

        StandardHttpExceptionHandlersFactory
                .create()
                .forEach(handler -> {
                    final Optional<HttpExceptionHandler<?, ?>> exceptionHandler = resolver.get(handler.resolveThrowbleType());
                    if(exceptionHandler.isEmpty()) {
                        resolver.add(handler);
                    }
                });

        return resolver;
    }

}
