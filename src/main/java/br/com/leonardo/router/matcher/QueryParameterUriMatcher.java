package br.com.leonardo.router.matcher;

public class QueryParameterUriMatcher implements UriMatcher {

    @Override
    public boolean match(String resolverUri, String inputUri) {
        final String[] resolverUriParts = inputUri.split("\\?");
        return resolverUriParts[0].equals(resolverUri);
    }

}
