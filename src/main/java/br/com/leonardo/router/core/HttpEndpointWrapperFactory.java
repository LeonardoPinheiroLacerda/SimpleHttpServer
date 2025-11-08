package br.com.leonardo.router.core;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.request.map.HeaderMap;
import br.com.leonardo.http.request.map.PathVariableMap;
import br.com.leonardo.http.request.map.QueryParameterMap;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import br.com.leonardo.router.extractor.HeaderExtractor;
import br.com.leonardo.router.extractor.PathVariableExtractor;
import br.com.leonardo.router.extractor.QueryParameterExtractor;

import java.util.Set;

public class HttpEndpointWrapperFactory {

    public static HttpEndpointWrapper<?, ?> create(HttpEndpoint<?, ?> httpEndpoint, HttpRequestData requestData) {

        final RequestLine requestLine = requestData.requestLine();
        final Set<HttpHeader> headers = requestData.headers();
        final byte[] body = requestData.body();


        final PathVariableMap pathMap = PathVariableExtractor.extract(requestLine, httpEndpoint);
        final QueryParameterMap queryMap = QueryParameterExtractor.extract(requestLine);
        final HeaderMap headerMap = HeaderExtractor.extract(headers);

        return new HttpEndpointWrapper<>(
                httpEndpoint,
                body,
                new HttpRequest<>(requestLine, headerMap, null, pathMap, queryMap)
        );
    }

}
