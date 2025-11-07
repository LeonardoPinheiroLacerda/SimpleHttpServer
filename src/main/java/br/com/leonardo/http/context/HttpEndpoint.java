package br.com.leonardo.http.context;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.middleware.Middleware;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.request.PathVariableMap;
import br.com.leonardo.http.response.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public abstract class HttpEndpoint<I, O> {

    private String uri;
    private HttpMethod method;
    private List<Middleware> middlewares = new CopyOnWriteArrayList<>();

    protected abstract HttpResponse<O> handle(HttpRequest<I> request) throws HttpException;

    public void runMiddlewares(RequestLine requestLine, Set<HttpHeader> headers, byte[] body, PathVariableMap pathVariableMap) throws HttpException {
        if(this.middlewares.isEmpty()) {
            return;
        }
        this.middlewares
                .getFirst()
                .handle(new HttpRequest<>(requestLine, headers, body, pathVariableMap));
    }

    public HttpResponse<O> createResponse(RequestLine requestLine, Set<HttpHeader> headers, byte[] body, PathVariableMap pathVariables) throws IOException {
        if(resolveInputType().equals(Void.class)) {
            final HttpRequest<I> request = new HttpRequest<>(requestLine, headers, null, pathVariables);
            return this.handle(request);
        }

        final ObjectMapper mapper = new ObjectMapper();
        I castedBody = mapper.readValue(body, mapper.constructType(resolveInputType()));
        final HttpRequest<I> request = new HttpRequest<>(requestLine, headers, castedBody, pathVariables);


        return this.handle(request);
    }

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
        if(!this.getMiddlewares().isEmpty()) {
            this.getMiddlewares()
                .getLast()
                .setNext(middleware);
        }

        this.getMiddlewares()
            .add(middleware);
    }

}
