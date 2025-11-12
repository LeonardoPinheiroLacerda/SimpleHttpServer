package br.com.leonardo.parser.factory.model;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpMethod;
import br.com.leonardo.http.RequestLine;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import java.util.Set;

class HttpRequestDataTest {

    @Test
    void shouldBeEquals() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users",
                "HTTP/1.1"
        );

        Set<HttpHeader> headers = Sets.set();

        byte[] body = new byte[4096];

        //When
        HttpRequestData httpRequestData = new HttpRequestData(requestLine, headers, body);
        HttpRequestData httpRequestData2 = new HttpRequestData(requestLine, headers, body);

        //Then
        Assertions
                .assertThat(httpRequestData)
                .isEqualTo(httpRequestData2);

    }

    @Test
    void shouldBeEqualsUsingHashCode() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users",
                "HTTP/1.1"
        );

        Set<HttpHeader> headers = Sets.set();

        byte[] body = new byte[4096];

        //When
        int httpRequestData = new HttpRequestData(requestLine, headers, body).hashCode();
        int httpRequestData2 = new HttpRequestData(requestLine, headers, body).hashCode();

        //Then
        Assertions
                .assertThat(httpRequestData)
                .isEqualTo(httpRequestData2);

    }

    @Test
    void shouldHaveToStringValue() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users",
                "HTTP/1.1"
        );

        Set<HttpHeader> headers = Sets.set();

        byte[] body = new byte[4096];

        //When
        String httpRequestData = new HttpRequestData(requestLine, headers, body).toString();

        //Then
        Assertions
                .assertThat(httpRequestData)
                .isNotNull();

    }




}