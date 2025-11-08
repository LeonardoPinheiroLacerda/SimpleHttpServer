package br.com.leonardo.router.matcher;

public interface UriMatcher {
    boolean match(String inputUri, String resolverUri);
}
