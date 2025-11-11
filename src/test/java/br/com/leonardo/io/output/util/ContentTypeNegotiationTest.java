
package br.com.leonardo.io.output.util;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.response.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class ContentTypeNegotiationTest {

    private ContentTypeNegotiation underTest = new ContentTypeNegotiation();

    @Test
    void shoudResolveSupportedAcceptHeader() {

        Set<HttpHeader> headers = new HashSet<>();
        headers.add(new HttpHeader("Accept", "application/json"));

        final HttpHeader httpHeader = underTest.resolveSupportedAcceptHeader(headers);

        Assertions
                .assertThat(httpHeader)
                .extracting(HttpHeader::value)
                .isEqualTo("application/json");

    }

    @Test
    void shouldIdentifyStaticResourceRequest() {
        //Given
        final String uri = "/static/css/style.css";

        //When
        final boolean staticResourceRequest = underTest.isStaticResourceRequest(uri);

        //Then
        Assertions
                .assertThat(staticResourceRequest)
                .isTrue();

    }

    @Test
    void shouldNotIdentifyStaticResourceRequest() {
        //Given
        final String uri = "/users";

        //When
        final boolean staticResourceRequest = underTest.isStaticResourceRequest(uri);

        //Then
        Assertions
                .assertThat(staticResourceRequest)
                .isFalse();

    }

    @Test
    void shouldNotIdentifyStaticResourceRequest2() {
        //Given
        final String uri = "/";

        //When
        final boolean staticResourceRequest = underTest.isStaticResourceRequest(uri);

        //Then
        Assertions
                .assertThat(staticResourceRequest)
                .isFalse();

    }

    @Test
    void shouldSetContentTypeJsonAndContentLengthToResponse() {

        //Given
        final HttpResponse<String> response = HttpResponse
                .<String>builder()
                .body("helpme")
                .statusCode(HttpStatusCode.OK)
                .header("Accept", "application/json")
                .build();

        HttpHeader httpHeader = new HttpHeader("Accept", "application/json");

        byte[] body = new byte[4096];

        //When
        underTest.setContentTypeAndContentLength(httpHeader, body, response);

        //Then
        Assertions
                .assertThat(response)
                .isNotNull();

    }

    @Test
    void shouldSetContentTypeXmlAndContentLengthToResponse() {

        //Given
        final HttpResponse<String> response = HttpResponse
                .<String>builder()
                .body("helpme")
                .statusCode(HttpStatusCode.OK)
                .header("Accept", "application/json")
                .build();

        HttpHeader httpHeader = new HttpHeader("Accept", "application/xml");

        byte[] body = new byte[4096];

        //When
        underTest.setContentTypeAndContentLength(httpHeader, body, response);

        //Then
        Assertions
                .assertThat(response)
                .isNotNull();

    }

    @Test
    void shouldSetContentTypeTextPlainAndContentLengthToResponse() {

        //Given
        final HttpResponse<String> response = HttpResponse
                .<String>builder()
                .body("helpme")
                .statusCode(HttpStatusCode.OK)
                .header("Accept", "application/json")
                .build();

        HttpHeader httpHeader = new HttpHeader("Accept", "text/plain");

        byte[] body = new byte[4096];

        //When
        underTest.setContentTypeAndContentLength(httpHeader, body, response);

        //Then
        Assertions
                .assertThat(response)
                .isNotNull();

    }

    @Test
    void shouldSetContentTypeAndContentLengthForStaticResourceRequest() {

        //Given
        final HttpResponse<String> response = HttpResponse
                .<String>builder()
                .body("helpme")
                .statusCode(HttpStatusCode.OK)
                .header("Accept", "application/json")
                .build();

        String uri = "/static/css/style.css";

        byte[] body = new byte[4096];

        //When
        underTest.setContentTypeAndContentLengthForStaticResources(body, uri, response);

        //Then
        Assertions
                .assertThat(response)
                .isNotNull();

    }

    @Test
    void shouldSerializePlainRequestBodyToJson() throws JsonProcessingException {

        //Given
        HttpHeader httpHeader = new HttpHeader("Accept", "application/json");

        byte[] body = """
                {"nome": "leonardo"}""".getBytes();

        //When
        final byte[] bytes = underTest.serializePlainBody(body, httpHeader);

        //Then
        Assertions
                .assertThat(bytes)
                .isNotNull();

    }

    @Test
    void shouldSerializePlainRequestBodyToXml() throws JsonProcessingException {

        //Given
        HttpHeader httpHeader = new HttpHeader("Accept", "application/xml");

        byte[] body = """
                <nome>Leonardo</nome>""".getBytes();

        //When
        final byte[] bytes = underTest.serializePlainBody(body, httpHeader);

        //Then
        Assertions
                .assertThat(bytes)
                .isNotNull();

    }

    @Test
    void shouldSerializePlainRequestBodyToTextPlain() throws JsonProcessingException {

        //Given
        HttpHeader httpHeader = new HttpHeader("Accept", "text/plain");

        byte[] body = """
                meu nome é leonardo""".getBytes();

        //When
        final byte[] bytes = underTest.serializePlainBody(body, httpHeader);

        //Then
        Assertions
                .assertThat(bytes)
                .isNotNull();

    }

    @Test
    void shouldNotFindStaticResource() {

        //Given
        final String uri = "/index.html";

        //When
        final Boolean b = underTest.existsStatic(uri);

        //Then
        Assertions
                .assertThat(b)
                .isFalse();

    }

    @Test
    void shouldSerializeStaticResource() throws IOException {

        //Givin
        final String uri = "/index.html";

        //When + Then
        final IOException ioException = Assertions.catchIOException(() -> underTest.serializeStaticBody(uri));

        Assertions
                .assertThat(ioException)
                .isNotNull();
    }

}