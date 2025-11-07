package br.com.leonardo.parser;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpMethod;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestLineParser {

    private static final Pattern pattern = Pattern.compile("^(OPTIONS|GET|HEAD|POST|PUT|DELETE|TRACE|CONNECT)\\s(/.*)\\s(HTTP/[0-9]\\.[0-9])$", Pattern.MULTILINE);

    protected static RequestLine parseRequestLine(String rawRequest) {

        Matcher matcher = pattern.matcher(rawRequest);

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
