package br.com.leonardo.enums;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ContentTypeTest {

    @ParameterizedTest
    @EnumSource(ContentType.class)
    void ensureAllContentTypesHaveNonNullAndNonEmptyType(ContentType contentType) {
        Assertions.assertThat(contentType.getType())
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void shouldReturnCorrectTypeForApplicationJson() {
        // Given
        ContentType contentType = ContentType.APPLICATION_JSON;

        // Then
        Assertions.assertThat(contentType.getType()).isEqualTo("application/json");
    }

    @Test
    void shouldReturnCorrectTypeForTextHtml() {
        // Given
        ContentType contentType = ContentType.TEXT_HTML;

        // Then
        Assertions.assertThat(contentType.getType()).isEqualTo("text/html");
    }
}
