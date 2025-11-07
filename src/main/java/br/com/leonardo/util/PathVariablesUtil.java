package br.com.leonardo.util;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.context.HttpEndpoint;
import br.com.leonardo.http.request.PathVariableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathVariablesUtil {

    private static final Pattern pathVariablePattern = Pattern.compile("^\\{(.*)}$");

    public static PathVariableMap extract(RequestLine requestLine, HttpEndpoint<?, ?> handler) {

        final String requestUri = requestLine.uri().split("\\?")[0];
        final String[] requestUriChunks = requestUri.split("/");

        final String handlerUri = handler.getUri();
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

    public static boolean match(String incomingUri, String templateUri) {
        final String[] endpointUriParts = incomingUri.split("/");
        final String[] requestUriParts = templateUri.split("/");

        if (endpointUriParts.length != requestUriParts.length) {
            return false;
        }

        for (int i = 0; i < endpointUriParts.length; i++) {
            final String endpointPart = endpointUriParts[i];
            final String requestPart = requestUriParts[i];

            final Matcher matcher = pathVariablePattern.matcher(endpointPart);
            if (!matcher.find() && !endpointPart.equals(requestPart)) {
                return false;
            }
        }

        return true;
    }
}
