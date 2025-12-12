package br.com.leonardo.context.scanner;

import br.com.leonardo.context.resolver.HttpEndpointResolver;
import br.com.leonardo.context.resolver.HttpExceptionHandlerResolver;
import br.com.leonardo.context.resolver.ResolversContextHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// A dummy class located in this package to provide a base for scanning.
class DummyBaseClassForScanning {
}

@ExtendWith(MockitoExtension.class)
class ScannersTest {

    @Mock
    private HttpEndpointResolver mockEndpointResolver;

    @Mock
    private HttpExceptionHandlerResolver mockExceptionHandlerResolver;

    @Test
    void shouldCorrectlyOrchestrateScannersAndCreateContext_whenScanIsCalled() {
        // Given
        try (MockedConstruction<EndpointScanner> mockedEndpointScanner = Mockito.mockConstruction(EndpointScanner.class,
                (mock, context) -> when(mock.scan(any(Reflections.class))).thenReturn(mockEndpointResolver));
             MockedConstruction<ExceptionHandlerScanner> mockedExceptionScanner = Mockito.mockConstruction(ExceptionHandlerScanner.class,
                (mock, context) -> when(mock.scan(any(Reflections.class))).thenReturn(mockExceptionHandlerResolver));
             MockedConstruction<Reflections> mockedReflections = Mockito.mockConstruction(Reflections.class)) {

            // When
            ResolversContextHolder resultHolder = Scanners.scan(DummyBaseClassForScanning.class);

            // Then
            assertThat(resultHolder).isNotNull();
            assertThat(resultHolder.getHttpEndpointResolver()).isSameAs(mockEndpointResolver);
            assertThat(resultHolder.getHttpExceptionHandlerResolver()).isSameAs(mockExceptionHandlerResolver);

            // Verify that new instances of scanners were created
            assertThat(mockedEndpointScanner.constructed()).hasSize(1);
            assertThat(mockedExceptionScanner.constructed()).hasSize(1);

            // Verify that a Reflections object was created
            assertThat(mockedReflections.constructed()).hasSize(1);

            // Verify that the scan method was called on the new scanner instances
            EndpointScanner endpointScannerInstance = mockedEndpointScanner.constructed().get(0);
            Mockito.verify(endpointScannerInstance).scan(mockedReflections.constructed().get(0));

            ExceptionHandlerScanner exceptionScannerInstance = mockedExceptionScanner.constructed().get(0);
            Mockito.verify(exceptionScannerInstance).scan(mockedReflections.constructed().get(0));
        }
    }
}
