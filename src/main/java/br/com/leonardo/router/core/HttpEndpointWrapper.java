package br.com.leonardo.router.core;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.middleware.Middleware;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
        try {

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
                            request.withBody(castedBody)
                    );

        } catch (NumberFormatException e) {
            throw new HttpException("Invalid number format " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR, request.uri(), e);
        } catch (NoSuchElementException e) {
            throw new HttpException(e.getMessage() + "' is missing.", HttpStatusCode.INTERNAL_SERVER_ERROR, request.uri(), e);
        } catch (IllegalArgumentException e) {
            throw new HttpException("Invalid argument " + e.getMessage(), HttpStatusCode.INTERNAL_SERVER_ERROR, request.uri(), e);
        } catch (Exception e) {
            throw new HttpException("Something unexpected happened", HttpStatusCode.INTERNAL_SERVER_ERROR, request.uri(), e);
        }
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
