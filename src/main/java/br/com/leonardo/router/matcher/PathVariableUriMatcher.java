package br.com.leonardo.router.matcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathVariableUriMatcher implements UriMatcher {

    private static final Pattern pathVariablePattern = Pattern.compile("^\\{(.*)}$");

    @Override
    public boolean match(String resolverUri, String inputUri) {

        final String[] inputUriParts = inputUri.split("/");
        final String[] resolverUriParts = resolverUri.split("/");

        if (inputUriParts.length != resolverUriParts.length) {
            return false;
        }

        for (int i = 0; i < resolverUriParts.length; i++) {
            final String endpointPart = resolverUriParts[i];
            final String requestPart = inputUriParts[i];

            final Matcher matcher = pathVariablePattern.matcher(endpointPart);
            if (!matcher.find() && !endpointPart.equals(requestPart)) {
                return false;
            }
        }

        return true;
    }

}
