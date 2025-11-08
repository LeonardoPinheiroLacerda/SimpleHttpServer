package br.com.leonardo.parser.factory.model;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;

import java.util.Set;

public record HttpRequestData(RequestLine requestLine,
                              Set<HttpHeader> headers,
                              byte[] body) {
}
