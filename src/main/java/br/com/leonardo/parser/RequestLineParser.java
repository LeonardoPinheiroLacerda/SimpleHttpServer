package br.com.leonardo.parser;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.RequestLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLineParser {

    private RequestLineParser() {}

    private static final Pattern PATTERN = Pattern.compile("^(OPTIONS|GET|HEAD|POST|PUT|DELETE|TRACE|CONNECT)\\s(/.*)\\s(HTTP/\\d\\.\\d)$", Pattern.MULTILINE);

    public static RequestLine parseRequestLine(String rawRequest) {

        Matcher matcher = PATTERN.matcher(rawRequest);

        if(matcher.find()) {
            String methodStr = matcher.group(1);
            String uri = matcher.group(2);
            String version = matcher.group(3);

            HttpMethod method = HttpMethod.valueOf(methodStr);

            return new RequestLine(method, uri, version);
        }

        throw new HttpException("Invalid request line: " + rawRequest, HttpStatusCode.BAD_REQUEST, null);

    }


}
