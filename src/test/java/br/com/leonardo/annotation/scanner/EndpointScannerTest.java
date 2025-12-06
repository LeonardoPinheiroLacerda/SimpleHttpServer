package br.com.leonardo.annotation.scanner;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class EndpointScannerTest {

    private final EndpointScanner scanner = new EndpointScanner();

    @Test
    void shouldScanEndpoint() {
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> scanner.scan(this.getClass()));
    }
}