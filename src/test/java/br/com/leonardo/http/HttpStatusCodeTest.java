package br.com.leonardo.http;

import br.com.leonardo.enums.HttpStatusCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpStatusCodeTest {

    @Test
    void shouldHaveCorrectCodeAndTextForOK() {
        // Given
        HttpStatusCode statusCode = HttpStatusCode.OK;

        // Then
        Assertions.assertThat(statusCode.getCode()).isEqualTo(200);
        Assertions.assertThat(statusCode.getText()).isEqualTo("OK");
    }

    @Test
    void shouldHaveCorrectCodeAndTextForNotFound() {
        // Given
        HttpStatusCode statusCode = HttpStatusCode.NOT_FOUND;

        // Then
        Assertions.assertThat(statusCode.getCode()).isEqualTo(404);
        Assertions.assertThat(statusCode.getText()).isEqualTo("Not Found");
    }

    @Test
    void shouldHaveCorrectCodeAndTextForInternalServerError() {
        // Given
        HttpStatusCode statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR;

        // Then
        Assertions.assertThat(statusCode.getCode()).isEqualTo(500);
        Assertions.assertThat(statusCode.getText()).isEqualTo("Internal Server Error");
    }

    @ParameterizedTest
    @EnumSource(HttpStatusCode.class)
    void ensureAllStatusCodesHavePositiveCodeAndNonNullText(HttpStatusCode code) {
        Assertions.assertThat(code.getCode()).isGreaterThan(0);
        Assertions.assertThat(code.getText()).isNotNull().isNotBlank();
    }
}