package br.com.leonardo.http.middleware;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.request.HttpRequest;
import lombok.Setter;

public abstract class Middleware {

    @Setter
    private Middleware next;

    public abstract void run(HttpRequest<?> request) throws HttpMiddlewareException;

    public void handle(HttpRequest<?> request) throws HttpMiddlewareException {
        try {
            this.run(request);

            if (this.next != null)
                this.next.handle(request);

        } catch (HttpException e) {
            throw new HttpMiddlewareException(e.getMessage(), e.getStatusCode(), e.getPath());
        } catch (Exception e) {
            throw new HttpMiddlewareException("Something unexpected happened on middlewares", HttpStatusCode.INTERNAL_SERVER_ERROR, request.uri().toString());
        }
    }

}
