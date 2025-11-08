package br.com.leonardo.server;

import br.com.leonardo.annotation.scanner.EndpointScanner;
import br.com.leonardo.router.core.HttpEndpointResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ServerRunner {

    public static void serve(Class<?> clazz) {

        final HttpEndpointResolver resolver = new HttpEndpointResolver();

        EndpointScanner scanner = new EndpointScanner(resolver);
        scanner.scan(clazz);

        try (Server server = new Server(resolver)){
            server.start();
        } catch (IOException e) {
            log.error("Failed to start server", e);
        }

    }
}
