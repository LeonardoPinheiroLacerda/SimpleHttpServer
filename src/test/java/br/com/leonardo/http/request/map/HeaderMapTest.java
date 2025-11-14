package br.com.leonardo.http.request.map;

import br.com.leonardo.enums.ContentTypeEnum;
import br.com.leonardo.enums.HttpHeaderEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

class HeaderMapTest {

    @Test
    void shouldGetStringValue() {
        HeaderMap headerMap = new HeaderMap(Map.of(HttpHeaderEnum.CONTENT_TYPE.getName(), ContentTypeEnum.APPLICATION_JSON.getType()));
        Assertions.assertThat(headerMap.getString(HttpHeaderEnum.CONTENT_TYPE.getName())).isEqualTo(Optional.of(ContentTypeEnum.APPLICATION_JSON.getType()));
    }

    @Test
    void shouldReturnEmptyOptionalForMissingString() {
        HeaderMap headerMap = new HeaderMap(Map.of());
        Assertions.assertThat(headerMap.getString(HttpHeaderEnum.CONTENT_TYPE.getName())).isEmpty();
    }

    @Test
    void shouldGetIntegerValue() {
        HeaderMap headerMap = new HeaderMap(Map.of(HttpHeaderEnum.CONTENT_LENGTH.getName(), "123"));
        Assertions.assertThat(headerMap.getInteger(HttpHeaderEnum.CONTENT_LENGTH.getName())).isEqualTo(Optional.of(123));
    }

    @Test
    void shouldGetLongValue() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Request-ID", "1234567890123"));
        Assertions.assertThat(headerMap.getLong("X-Request-ID")).isEqualTo(Optional.of(1234567890123L));
    }

    @Test
    void shouldThrowNumberFormatExceptionForInvalidLong() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Request-ID", "abc"));
        Assertions.assertThatThrownBy(() -> headerMap.getLong("X-Request-ID"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void shouldGetBooleanValueTrue() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Custom-Flag", "true"));
        Assertions.assertThat(headerMap.getBoolean("X-Custom-Flag")).isEqualTo(Optional.of(true));
    }

    @Test
    void shouldGetBooleanValueFalse() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Custom-Flag", "false"));
        Assertions.assertThat(headerMap.getBoolean("X-Custom-Flag")).isEqualTo(Optional.of(false));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForInvalidBoolean() {
        HeaderMap headerMap = new HeaderMap(Map.of("X-Custom-Flag", "not-a-boolean"));
        Assertions.assertThatThrownBy(() -> headerMap.getBoolean("X-Custom-Flag"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Header 'X-Custom-Flag' is not a valid boolean. Received: not-a-boolean");
    }

    @Test
    void shouldReturnTrueWhenHeaderExists() {
        HeaderMap headerMap = new HeaderMap(Map.of(HttpHeaderEnum.ACCEPT.getName(), ContentTypeEnum.APPLICATION_JSON.getType()));
        Assertions.assertThat(headerMap.exists(HttpHeaderEnum.ACCEPT.getName())).isTrue();
    }

    @Test
    void shouldReturnFalseWhenHeaderDoesNotExist() {
        HeaderMap headerMap = new HeaderMap(Map.of());
        Assertions.assertThat(headerMap.exists(HttpHeaderEnum.ACCEPT.getName())).isFalse();
    }
}