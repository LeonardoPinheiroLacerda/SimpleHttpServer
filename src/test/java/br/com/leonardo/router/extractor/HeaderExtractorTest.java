package br.com.leonardo.router.extractor;

import br.com.leonardo.enums.ContentTypeEnum;
import br.com.leonardo.enums.HttpHeaderEnum;
import br.com.leonardo.http.request.map.HeaderMap;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import java.util.Set;

class HeaderExtractorTest {


    @Test
    void shouldExtractHeaders() {
        //Given
        Set<br.com.leonardo.http.HttpHeader> headers = Sets.set(
                new br.com.leonardo.http.HttpHeader(HttpHeaderEnum.CONTENT_TYPE.getName(), ContentTypeEnum.APPLICATION_JSON.getType()),
                new br.com.leonardo.http.HttpHeader(HttpHeaderEnum.ACCEPT.getName(), ContentTypeEnum.APPLICATION_JSON.getType())
        );

        //When
        final HeaderMap extract = HeaderExtractor.extract(headers);

        //Then
        Assertions
                .assertThat(extract)
                .extracting(
                        h -> h.getString(HttpHeaderEnum.CONTENT_TYPE.getName()).orElse(null),
                        h -> h.getString(HttpHeaderEnum.ACCEPT.getName()).orElse(null),
                        h -> h.getString(HttpHeaderEnum.AUTHORIZATION.getName()).orElse(null)
                )
                .containsExactly(ContentTypeEnum.APPLICATION_JSON.getType(), ContentTypeEnum.APPLICATION_JSON.getType(), null);
    }

}
