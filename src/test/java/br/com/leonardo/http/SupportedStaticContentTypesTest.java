package br.com.leonardo.http;

import br.com.leonardo.enums.SupportedStaticContentTypes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SupportedStaticContentTypesTest {

    @ParameterizedTest
    @CsvSource({
            "html, text/html",
            "css, text/css",
            "js, application/javascript",
            "png, image/png",
            "jpg, image/jpeg",
            "jpeg, image/jpeg",
            "gif, image/gif",
            "svg, image/svg+xml",
            "ico, image/x-icon",
            "json, application/json",
            "xml, application/xml",
            "txt, text/plain",
            "pdf, application/pdf",
            "mp4, video/mp4",
            "mp3, audio/mpeg"
    })
    void shouldReturnCorrectMediaTypeForExtension(String extension, String expectedMediaType) {
        // When
        String mediaType = SupportedStaticContentTypes.getMediaType(extension);

        // Then
        Assertions.assertThat(mediaType).isEqualTo(expectedMediaType);
    }

    @Test
    void shouldReturnCorrectMediaTypeForExtensionCaseInsensitive() {
        // When
        String mediaType = SupportedStaticContentTypes.getMediaType("HTML");

        // Then
        Assertions.assertThat(mediaType).isEqualTo("text/html");
    }

    @Test
    void shouldReturnNullForUnsupportedExtension() {
        // When
        String mediaType = SupportedStaticContentTypes.getMediaType("unsupported");

        // Then
        Assertions.assertThat(mediaType).isNull();
    }
}
