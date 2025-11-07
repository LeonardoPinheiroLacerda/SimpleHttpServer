package br.com.leonardo.parser;

import br.com.leonardo.http.HttpHeader;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHeaderParser {

    private final static Pattern pattern = Pattern.compile("^(.+):\\s(.+)$", Pattern.MULTILINE);
    private final static String doubleCrlf = "\r\n\r\n";

    protected static Set<HttpHeader> parseRequestHeaders(String rawRequest) {

        Set<HttpHeader> headers = new HashSet<>();

        final String[] chunks = rawRequest.split(doubleCrlf);

        final Matcher matcher = pattern.matcher(chunks[0]);

        while (matcher.find()) {
            final String headerName = matcher.group(1);
            final String headerValue = matcher.group(2);

            headers.add(new HttpHeader(headerName, headerValue));
        }

        return headers;

    }

}
