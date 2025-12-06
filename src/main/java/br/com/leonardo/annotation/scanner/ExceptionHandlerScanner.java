package br.com.leonardo.annotation.scanner;

import br.com.leonardo.annotation.ExceptionHandler;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.HttpExceptionHandlerResolver;
import br.com.leonardo.exception.handler.StandardHttpExceptionHandlersFactory;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ExceptionHandlerScanner {

    public HttpExceptionHandlerResolver scan(Class<?> clazz) {

        final HttpExceptionHandlerResolver resolver = new HttpExceptionHandlerResolver();

        final String pack = clazz.getPackage().getName();
        final Reflections reflections = new Reflections(pack);

        final Set<Class<?>> types = reflections
                .getTypesAnnotatedWith(ExceptionHandler.class);

        for (Class<?> exceptionHandler : types) {
            try {
                final HttpExceptionHandler<?, ?> httpExceptionHandler =
                        (HttpExceptionHandler<?, ?>) exceptionHandler
                                .getDeclaredConstructor()
                                .newInstance();

                if(resolver.get(httpExceptionHandler.resolveThrowbleType(), false).isPresent()) {
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
                    final Optional<HttpExceptionHandler<?, ?>> exceptionHandler = resolver.get(handler.resolveThrowbleType(), false);
                    if(exceptionHandler.isEmpty()) {
                        resolver.add(handler);
                    }
                });

        return resolver;
    }

}
