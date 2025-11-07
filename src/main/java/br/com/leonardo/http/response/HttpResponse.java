package br.com.leonardo.http.response;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter(AccessLevel.PACKAGE)
public class HttpResponse<O> {

    private HttpStatusCode statusCode;
    private Set<HttpHeader> headers;
    private O body;

    public static <O> HttpResponseBuilder<O> builder() {
        return new HttpResponseBuilder<>(new HttpResponse<>());
    }

    public void addHeader(String name, Object value) {
        this.headers.add(new HttpHeader(name, value.toString()));
    }
}
