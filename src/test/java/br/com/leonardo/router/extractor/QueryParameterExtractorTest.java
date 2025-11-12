package br.com.leonardo.router.extractor;

import br.com.leonardo.enums.HttpMethod;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.request.map.QueryParameterMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryParameterExtractorTest {



    @Test
    void shouldExtractQueryParameterFromRequestLine() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users?name=leonardo",
                "HTTP/1.1"
        );

        //When
        final QueryParameterMap extract = QueryParameterExtractor.extract(requestLine);

        //Then
        Assertions
                .assertThat(extract)
                .extracting(p -> p.getString("name").orElse(null))
                .isEqualTo("leonardo");

    }

    @Test
    void shouldNotExtractQueryParameterFromRequestLineBecauseNotMatch() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users",
                "HTTP/1.1"
        );

        //When
        final QueryParameterMap extract = QueryParameterExtractor.extract(requestLine);

        //Then
        Assertions
                .assertThat(extract)
                .extracting(p -> p.getString("name").orElse(null))
                .isNull();

    }

    @Test
    void shouldNotExtractQueryParameterFromRequestLineBecauseItsInvalid() {

        //Given
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/users?name",
                "HTTP/1.1"
        );

        //When
        final QueryParameterMap extract = QueryParameterExtractor.extract(requestLine);

        //Then
        Assertions
                .assertThat(extract)
                .extracting(p -> p.getString("name").orElse(null))
                .isNull();

    }

}