package br.com.leonardo.annotation.scanner;

import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.router.core.middleware.Middleware;
import br.com.leonardo.http.request.HttpRequest;

public class Middleware1 extends Middleware {
    @Override
    public void run(HttpRequest<?> request) throws HttpMiddlewareException {
        System.out.println("eu sou um middleware :)");
    }
}
