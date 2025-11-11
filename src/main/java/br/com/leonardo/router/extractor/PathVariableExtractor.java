package br.com.leonardo.router.extractor;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.router.core.HttpEndpoint;
import br.com.leonardo.http.request.map.PathVariableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathVariableExtractor {

    private PathVariableExtractor() {}

    private static final Pattern pathVariablePattern = Pattern.compile("^\\{(.*)}$");

    public static PathVariableMap extract(RequestLine requestLine, HttpEndpoint<?, ?> httpEndpoint) {

        final String requestUri = requestLine.uri().split("\\?")[0];
        final String[] requestUriChunks = requestUri.split("/");

        final String handlerUri = httpEndpoint.getUri();
        final String[] HandlerUriChunks = handlerUri.split("/");

        final Map<String, String> pathVariables = new HashMap<>();

        for (int i = 0; i < HandlerUriChunks.length; i++) {
            final String endpointPart = HandlerUriChunks[i];
            final String requestPart = requestUriChunks[i];

            Matcher matcher = pathVariablePattern.matcher(endpointPart);
            if (matcher.find()) {
                final String name = matcher.group(1);
                pathVariables.put(name, requestPart);
            } else if (!endpointPart.equals(requestPart)) {
                throw new HttpException("Path variable mismatch", HttpStatusCode.INTERNAL_SERVER_ERROR, null);
            }

        }

        return new PathVariableMap(pathVariables);

    }

}
