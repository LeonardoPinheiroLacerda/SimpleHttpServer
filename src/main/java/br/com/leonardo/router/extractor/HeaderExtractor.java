package br.com.leonardo.router.extractor;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.request.map.HeaderMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HeaderExtractor {

    public static HeaderMap extract(Set<HttpHeader> headers) {
        Map<String, String> headerMap = new HashMap<>();
        headers.forEach(header -> headerMap.put(header.name(), header.value()));
        return new HeaderMap(headerMap);
    }

}
