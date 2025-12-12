package br.com.leonardo.server;

import br.com.leonardo.context.resolver.ResolversContextHolder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;

@ExtendWith(MockitoExtension.class)
class ServerTest {

    private Server server;

    @Mock
    private ResolversContextHolder resolversContextHolder;

    @Test
    void shouldConstructServer() throws Exception {
        try(
                MockedConstruction<ServerSocket> serverSocket = Mockito.mockConstruction(ServerSocket.class);
                MockedStatic<ExecutorService> executorService = Mockito.mockStatic(ExecutorService.class);
                MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class)
        ) {
            //When
            server = new Server(resolversContextHolder);

            //Then
            Assertions
                    .assertThat(server)
                    .isNotNull();
        }
    }

    @Test
    void shouldStartServerAndThrowSocketException() throws Exception {
        try(
                MockedConstruction<ServerSocket> serverSocket = Mockito.mockConstruction(ServerSocket.class);
                MockedStatic<ExecutorService> executorService = Mockito.mockStatic(ExecutorService.class);
                MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class)
        ) {
            //When
            server = new Server(resolversContextHolder);

            Runtime runtimeMock = Mockito
                    .mock(Runtime.class);

            runtime
                    .when(Runtime::getRuntime)
                    .thenReturn(runtimeMock);

            Mockito
                    .when(serverSocket.constructed().getFirst().accept())
                    .thenReturn(Mockito.mock(Socket.class))
                    .thenThrow(new SocketException("Socket closed"));

            server.start();

            //Then
            Assertions
                    .assertThat(server)
                    .isNotNull();
        }
    }

    @Test
    void shouldStartServerAndThrowIOException() throws Exception {
        try(
                MockedConstruction<ServerSocket> serverSocket = Mockito.mockConstruction(ServerSocket.class);
                MockedStatic<ExecutorService> executorService = Mockito.mockStatic(ExecutorService.class);
                MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class)
        ) {
            //When
            server = new Server(resolversContextHolder);

            Runtime runtimeMock = Mockito
                    .mock(Runtime.class);

            runtime
                    .when(Runtime::getRuntime)
                    .thenReturn(runtimeMock);

            Mockito
                    .when(serverSocket.constructed().getFirst().accept())
                    .thenReturn(Mockito.mock(Socket.class))
                    .thenThrow(new IOException("IOException"));

            server.start();

            //Then
            Assertions
                    .assertThat(server)
                    .isNotNull();
        }
    }

    @Test
    void shouldCloseServer() throws Exception {
        try(
                MockedConstruction<ServerSocket> serverSocket = Mockito.mockConstruction(ServerSocket.class);
                MockedStatic<ExecutorService> executorService = Mockito.mockStatic(ExecutorService.class);
                MockedStatic<Runtime> runtime = Mockito.mockStatic(Runtime.class)
        ) {
            //When
            server = new Server(resolversContextHolder);
            server.close();

            //Then
            Assertions
                    .assertThat(server)
                    .isNotNull();
        }
    }
}