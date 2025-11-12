package br.com.leonardo.http.response;

import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.enums.HttpStatusCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void shouldBuildHttpResponseWithAllProperties() {
        // Given
        HttpStatusCode expectedStatus = HttpStatusCode.OK;
        String expectedBody = "Test Body";
        HttpHeader expectedHeader = new HttpHeader("Content-Type", "text/plain");

        // When
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(expectedStatus)
                .header(expectedHeader.name(), expectedHeader.value())
                .body(expectedBody)
                .build();

        // Then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        Assertions.assertThat(response.getBody()).isEqualTo(expectedBody);
        Assertions.assertThat(response.getHeaders()).contains(expectedHeader);
    }

    @Test
    void shouldBuildHttpResponseWithNoBody() {
        // Given
        HttpStatusCode expectedStatus = HttpStatusCode.NO_CONTENT;

        // When
        HttpResponse<Void> response = HttpResponse.<Void>builder()
                .statusCode(expectedStatus)
                .build();

        // Then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        Assertions.assertThat(response.getBody()).isNull();
        Assertions.assertThat(response.getHeaders()).isNotNull().isEmpty();
    }

    @Test
    void shouldAddHeaderToExistingResponse() {
        // Given
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(HttpStatusCode.OK)
                .body("Initial Body")
                .build();

        // When
        response.addHeader("X-Custom-Header", "Value123");

        // Then
        Assertions.assertThat(response.getHeaders())
                .contains(new HttpHeader("X-Custom-Header", "Value123"));
    }

    @Test
    void shouldAllowMultipleHeadersWithSameName() {
        // When
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(HttpStatusCode.OK)
                .header("Set-Cookie", "session=123")
                .header("Set-Cookie", "user=leo")
                .build();

        // Then
        Assertions.assertThat(response.getHeaders())
                .contains(new HttpHeader("Set-Cookie", "session=123"))
                .contains(new HttpHeader("Set-Cookie", "user=leo"));
    }

    @Test
    void shouldBuildResponseAndThenAddHeader() {
        // When
        HttpResponse<String> response = HttpResponse.<String>builder()
                .statusCode(HttpStatusCode.CREATED)
                .body("Resource created")
                .build();

        response.addHeader("Location", "/users/123");

        // Then
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.CREATED);
        Assertions.assertThat(response.getBody()).isEqualTo("Resource created");
        Assertions.assertThat(response.getHeaders()).contains(new HttpHeader("Location", "/users/123"));
    }
}
