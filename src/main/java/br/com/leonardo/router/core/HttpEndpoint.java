package br.com.leonardo.router.core;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.middleware.Middleware;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public abstract class HttpEndpoint<I, O> {

    private String uri;
    private HttpMethod method;
    private List<Middleware> middlewares = new CopyOnWriteArrayList<>();

    protected abstract HttpResponse<O> handle(HttpRequest<I> request) throws HttpException;

    public Type resolveInputType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getActualTypeArguments()[0]; // <I>
        }
        return Object.class;
    }

    public Type resolveOutputType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getActualTypeArguments()[1]; // <O>
        }
        return Object.class;
    }

    public void addMiddleware(Middleware middleware) {
        if(!this.middlewares.isEmpty()) {
            this.middlewares
                    .getLast()
                    .setNext(middleware);
        }

        this.middlewares
                .add(middleware);
    }
}
