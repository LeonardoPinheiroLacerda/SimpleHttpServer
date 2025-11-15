package br.com.leonardo.router.core.middleware;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.exception.HttpMiddlewareException;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.request.HttpRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Middleware {

    private Middleware next;

    public abstract void run(HttpRequest<?> request) throws HttpMiddlewareException;

    public boolean hasNext() {
        return this.next != null;
    }

    public boolean isLast() {
        return !this.hasNext();
    }

    public boolean next(HttpRequest<?> request) throws HttpMiddlewareException {
        if(this.hasNext()) {
            this.next.run(request);
            return true;
        }
        return false;
    }

}
