package br.com.leonardo.http.middleware;

import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.http.request.HttpRequest;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class ConsumerMiddleware extends Middleware{

    private final Consumer<HttpRequest<?>> consumer;

    @Override
    public void run(HttpRequest<?> request) throws HttpMiddlewareException {
        consumer.accept(request);
    }

}
