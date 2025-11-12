package br.com.leonardo.server;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.io.ConnectionIOHandler;
import br.com.leonardo.router.core.HttpEndpointResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Server implements AutoCloseable {

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final HttpEndpointResolver resolver;

    private volatile boolean isRunning = true;

    public Server(HttpEndpointResolver resolver) throws IOException {
        this.serverSocket = new ServerSocket(ApplicationProperties.getPort());
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.resolver = resolver;
    }

    public void start() {
        log.info("Server started on port {}", serverSocket.getLocalPort());
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));

        while (isRunning) {
            try {
                Socket client = serverSocket.accept();
                log.trace("Accepted client from {}", client.getRemoteSocketAddress());
                executorService.submit(new ConnectionIOHandler(client, resolver));
                log.trace("Submitted client IO task for {}", client.getRemoteSocketAddress());
            } catch (SocketException e) {
                log.error("SocketException while accepting connections", e);
                isRunning = false;
            } catch (IOException e) {
                log.error("I/O error when accepting connections", e);
                isRunning = false;
            }
        }
    }

    @Override
    public void close() {
        if (!isRunning) return;
        isRunning = false;
        log.info("Shutting down server...");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.error("Error closing server socket", e);
        }
        shutdownExecutor();
        log.info("Server shut down successfully.");
    }

    private void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.error("Executor service did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
