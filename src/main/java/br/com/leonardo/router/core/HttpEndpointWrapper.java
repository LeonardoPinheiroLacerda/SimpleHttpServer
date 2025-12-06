package br.com.leonardo.router.core;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.router.core.middleware.Middleware;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public record HttpEndpointWrapper<I, O>(
        HttpEndpoint<I, O> endpoint,
        byte[] body,
        HttpRequest<I> request
) {

    public void runMiddlewares() throws HttpException {
        final List<Middleware> middlewares = this.endpoint.getMiddlewares();

        if (middlewares.isEmpty()) {
            return;
        }

        middlewares
                .getFirst()
                .run(request);
    }

    public HttpResponse<O> createResponse() throws Exception {

        I castedBody = switch (endpoint.resolveInputType().getTypeName()) {
            case "java.lang.Void" -> null;
            case "java.lang.String" -> (I) new String(body);

            default -> {
                final ObjectMapper mapper = new ObjectMapper();
                yield mapper.readValue(body, mapper.constructType(endpoint.resolveInputType()));
            }
        };

        if (castedBody == null) {
            return endpoint.handle(request);
        }

        return endpoint
                .handle(
                        new HttpRequest<>(
                                this.request.requestLine(),
                                this.request.headers(),
                                castedBody,
                                this.request.pathVariables(),
                                this.request.queryParameters(),
                                this.request.middlewareProperties()
                        )
                );


    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        HttpEndpointWrapper<?, ?> that = (HttpEndpointWrapper<?, ?>) o;
        return endpoint.equals(that.endpoint);
    }

    @Override
    public int hashCode() {
        return endpoint.hashCode();
    }

    @Override
    public String toString() {
        return "HttpEndpointWrapper{" +
                "endpoint=" + endpoint +
                ", body=" + body.length +
                ", request=" + request +
                '}';
    }
}
