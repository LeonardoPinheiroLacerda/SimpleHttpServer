package br.com.leonardo.router.matcher;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class PathVariableUriMatcherTest {

    private final PathVariableUriMatcher matcher = new PathVariableUriMatcher();

    @ParameterizedTest
    @CsvSource({
            "/users/123, /users/123",
            "/users/{id}, /users/456",
            "/users/{userId}/orders/{orderId}, /users/789/orders/abc",
            "/, /",
            "/{id}, /test",
            "/api/v1/{resource}/item/{itemId}, /api/v1/products/item/P001"
    })
    void shouldMatch(String resolverUri, String inputUri) {
        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "/users/{id}, /users/123/orders",
            "/users/123/orders, /users/{id}",
            "/users/admin, /users/guest",
            "/users/{userId}/profile, /users/123/settings",
            "/, /test",
            "/test, /",
            "/api/v1/users/{id}, /api/v2/users/123"
    })
    void shouldNotMatch(String resolverUri, String inputUri) {
        // When
        boolean result = matcher.match(resolverUri, inputUri);

        // Then
        assertThat(result).isFalse();
    }
}
