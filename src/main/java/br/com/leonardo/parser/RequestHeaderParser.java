package br.com.leonardo.parser;

import br.com.leonardo.http.HttpHeader;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHeaderParser {

    private RequestHeaderParser() {}

    private static final Pattern PATTERN = Pattern.compile("^([^:\\r\\n]+):\\s*(.+)$", Pattern.MULTILINE);
    private static final String DOUBLE_CRLF = "\r\n\r\n";

    public static Set<HttpHeader> parseRequestHeaders(String rawRequest) {

        Set<HttpHeader> headers = new HashSet<>();

        final String[] chunks = rawRequest.split(DOUBLE_CRLF);

        final Matcher matcher = PATTERN.matcher(chunks[0]);

        while (matcher.find()) {
            final String headerName = matcher.group(1);
            final String headerValue = matcher.group(2);

            headers.add(new HttpHeader(headerName, headerValue));
        }

        return headers;

    }

}
