package br.com.leonardo.http.response;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;

import java.util.HashSet;

public record HttpResponseBuilder<O>(HttpResponse<O> response) {

    public HttpResponseBuilder {
        response.setHeaders(new HashSet<>());
    }

    public HttpResponseBuilder<O> statusCode(HttpStatusCode statusCode) {
        this.response.setStatusCode(statusCode);
        return this;
    }

    public HttpResponseBuilder<O> header(String name, Object value) {
        this.response.getHeaders().add(new HttpHeader(name, value.toString()));
        return this;
    }

    public HttpResponseBuilder<O> body(O body) {
        this.response.setBody(body);
        return this;
    }

    public HttpResponse<O> build() {
        return this.response;
    }

}
