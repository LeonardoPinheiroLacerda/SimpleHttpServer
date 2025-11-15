package br.com.leonardo.enums;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ContentTypeEnumTest {

    @ParameterizedTest
    @EnumSource(ContentTypeEnum.class)
    void ensureAllContentTypesHaveNonNullAndNonEmptyType(ContentTypeEnum contentType) {
        Assertions.assertThat(contentType.getType())
                .isNotNull()
                .isNotBlank();
    }

    @Test
    void shouldReturnCorrectTypeForApplicationJson() {
        // Given
        ContentTypeEnum contentType = ContentTypeEnum.APPLICATION_JSON;

        // Then
        Assertions.assertThat(contentType.getType()).isEqualTo("application/json");
    }

    @Test
    void shouldReturnCorrectTypeForTextHtml() {
        // Given
        ContentTypeEnum contentType = ContentTypeEnum.TEXT_HTML;

        // Then
        Assertions.assertThat(contentType.getType()).isEqualTo("text/html");
    }
}
