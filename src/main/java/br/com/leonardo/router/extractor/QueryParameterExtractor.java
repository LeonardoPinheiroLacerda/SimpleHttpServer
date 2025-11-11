package br.com.leonardo.router.extractor;

import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.QueryParameterMap;

import java.util.HashMap;
import java.util.Map;

public class QueryParameterExtractor {

    private QueryParameterExtractor() {}

    public static QueryParameterMap extract(RequestLine requestLine) {

        final String url = requestLine.uri();
        final String[] chunks = url.split("\\?");

        if(chunks.length == 1) {
            return new QueryParameterMap(new HashMap<>());
        }

        final String values = chunks[1];
        final String[] params = values.split("&");

        Map<String, String> queryParameters = new HashMap<>();

        for(String param : params) {
            final String[] paramChunks = param.split("=");

            if(paramChunks.length == 1) {
                queryParameters.put(paramChunks[0], null);
            }else{
                queryParameters.put(paramChunks[0], paramChunks[1]);
            }
        }

        return new QueryParameterMap(queryParameters);
    }

}
