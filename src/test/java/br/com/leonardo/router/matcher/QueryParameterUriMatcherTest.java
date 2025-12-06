package br.com.leonardo.router.matcher;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParameterUriMatcherTest {

    private final QueryParameterUriMatcher matcher = new QueryParameterUriMatcher();

    @ParameterizedTest
    @CsvSource({
            "/products, /products/",
            "/products, /products/?id=1",
            "/products/, /products"
    })
    void shouldNotMatch_whenTrailingSlashMismatch_parameterized(String resolverUri, String inputUri) {
        assertThat(matcher.match(resolverUri, inputUri)).isFalse();
    }

}
