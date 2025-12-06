package br.com.leonardo.exception.handler;

import br.com.leonardo.exception.handler.impl.HttpHttpExceptionHandler;
import br.com.leonardo.exception.handler.impl.HttpMiddlewareHttpExceptionHandler;
import br.com.leonardo.exception.handler.impl.InternalServerErrorHttpExceptionHandler;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StandardHttpExceptionHandlersFactoryTest {

    @Test
    void shouldCreateAndReturnStandardHandlers() {
        // When
        Set<HttpExceptionHandler<?, ?>> handlers = StandardHttpExceptionHandlersFactory.create();

        // Then
        assertThat(handlers)
                .isNotNull()
                .hasSize(3)
                .hasAtLeastOneElementOfType(InternalServerErrorHttpExceptionHandler.class)
                .hasAtLeastOneElementOfType(HttpHttpExceptionHandler.class)
                .hasAtLeastOneElementOfType(HttpMiddlewareHttpExceptionHandler.class);
    }
}
