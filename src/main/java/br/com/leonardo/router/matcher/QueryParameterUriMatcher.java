package br.com.leonardo.router.matcher;

public class QueryParameterUriMatcher implements UriMatcher {

    @Override
    public boolean match(String inputUri, String resolverUri) {
        final String[] resolverUriParts = resolverUri.split("\\?");
        return resolverUriParts[0].equals(inputUri);
    }

}
