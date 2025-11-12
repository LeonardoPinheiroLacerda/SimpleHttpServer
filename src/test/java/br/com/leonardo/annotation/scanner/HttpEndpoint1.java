package br.com.leonardo.annotation.scanner;

import br.com.leonardo.annotation.Endpoint;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.router.core.HttpEndpoint;

@Endpoint(
        url = "/users",
        method = HttpMethod.GET,
        middlewares = Middleware1.class
)
public class HttpEndpoint1 extends HttpEndpoint<Void, String> {
    @Override
    protected HttpResponse<String> handle(HttpRequest<Void> request) throws HttpException {
        return null;
    }
}
