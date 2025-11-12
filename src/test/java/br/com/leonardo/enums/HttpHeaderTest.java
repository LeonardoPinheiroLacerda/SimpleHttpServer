package br.com.leonardo.enums;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpHeaderTest {

    @ParameterizedTest
    @EnumSource(HttpHeader.class)
    void ensureAllHttpHeadersHaveNonNullAndNonEmptyName(HttpHeader header) {
        Assertions.assertThat(header.getName())
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void shouldReturnCorrectNameForContentType() {
        // Given
        HttpHeader header = HttpHeader.CONTENT_TYPE;

        // Then
        Assertions.assertThat(header.getName()).isEqualTo("Content-Type");
    }

    @Test
    void shouldReturnCorrectNameForAccept() {
        // Given
        HttpHeader header = HttpHeader.ACCEPT;

        // Then
        Assertions.assertThat(header.getName()).isEqualTo("Accept");
    }
}
