package br.com.leonardo.router.core;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.middleware.Middleware;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public record HttpEndpointWrapper<I, O> (
        HttpEndpoint<I, O> endpoint,
        byte[] body,
        HttpRequest<I> request
) {

    public void runMiddlewares() throws HttpException {
        final List<Middleware> middlewares = this.endpoint.getMiddlewares();

        if(middlewares.isEmpty()) {
            return;
        }

        middlewares
                .getFirst()
                .handle(request);
    }

    public HttpResponse<O> createResponse() throws IOException {
        if(endpoint.resolveInputType().equals(Void.class)) {
            return endpoint.handle(request);
        }

        final ObjectMapper mapper = new ObjectMapper();
        I castedBody = mapper.readValue(body, mapper.constructType(endpoint.resolveInputType()));

        return endpoint
                .handle(
                        request.withBody(castedBody)
                );
    }

}
