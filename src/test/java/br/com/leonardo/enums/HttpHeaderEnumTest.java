package br.com.leonardo.enums;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class HttpHeaderEnumTest {

    @ParameterizedTest
    @EnumSource(HttpHeaderEnum.class)
    void ensureAllHttpHeadersHaveNonNullAndNonEmptyName(HttpHeaderEnum header) {
        Assertions.assertThat(header.getName())
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void shouldReturnCorrectNameForContentType() {
        // Given
        HttpHeaderEnum header = HttpHeaderEnum.CONTENT_TYPE;

        // Then
        Assertions.assertThat(header.getName()).isEqualTo("Content-Type");
    }

    @Test
    void shouldReturnCorrectNameForAccept() {
        // Given
        HttpHeaderEnum header = HttpHeaderEnum.ACCEPT;

        // Then
        Assertions.assertThat(header.getName()).isEqualTo("Accept");
    }
}
