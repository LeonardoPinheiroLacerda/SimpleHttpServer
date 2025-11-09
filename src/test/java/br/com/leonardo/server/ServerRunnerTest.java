package br.com.leonardo.server;

import br.com.leonardo.annotation.scanner.EndpointScanner;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.router.core.HttpEndpointResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith({MockitoExtension.class})
class ServerRunnerTest {

    @Test
    void shouldScanAndStartServer() {

        //Given
        Class<?> clazz = this.getClass();

        try(
                MockedConstruction<HttpEndpointResolver> resolver = Mockito.mockConstruction(HttpEndpointResolver.class);
                MockedConstruction<EndpointScanner> endpointScanner = Mockito.mockConstruction(EndpointScanner.class);
                MockedConstruction<Server> server = Mockito.mockConstruction(Server.class)
        ) {
            //When
            ServerRunner.serve(clazz);

            //Then
            Mockito
                    .verify(server.constructed().getFirst(), Mockito.atMostOnce())
                    .start();
        }

    }

    @Test
    void shouldNotScanAndStartServerBecauseIOExceptionIsThrown() {

        //Given
        Class<?> clazz = this.getClass();

        try(
                MockedConstruction<HttpEndpointResolver> resolver = Mockito.mockConstruction(HttpEndpointResolver.class);
                MockedConstruction<EndpointScanner> endpointScanner = Mockito.mockConstruction(EndpointScanner.class);
                MockedConstruction<Server> server = Mockito.mockConstruction(Server.class,
                        (mock, context) -> {
                            Mockito
                                    .doAnswer(invocationOnMock -> {
                                        throw new IOException("Something went wrong");
                                    })
                                    .when(mock).start();
                        }
                )
        ) {
            //When + Then
            Assertions
                    .assertThrows(ServerInitializationException.class, () -> ServerRunner.serve(clazz));

        }

    }

}