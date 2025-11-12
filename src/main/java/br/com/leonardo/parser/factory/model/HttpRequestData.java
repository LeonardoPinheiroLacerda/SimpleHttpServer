package br.com.leonardo.parser.factory.model;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;

import java.util.Set;

public record HttpRequestData(RequestLine requestLine,
                              Set<HttpHeader> headers,
                              byte[] body) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        HttpRequestData that = (HttpRequestData) o;
        return requestLine.equals(that.requestLine);
    }

    @Override
    public int hashCode() {
        return requestLine.hashCode();
    }

    @Override
    public String toString() {
        return "HttpRequestData{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", body=" + body.length +
                '}';
    }
}
