package br.com.leonardo.server;

import br.com.leonardo.annotation.scanner.EndpointScanner;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.router.core.HttpEndpointResolver;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ServerRunner {

    private ServerRunner() {}

    private static final String BANNER = """
             _____ _                 _        _   _ _____ ___________   _____                         \s
            /  ___(_)               | |      | | | |_   _|_   _| ___ \\ /  ___|                        \s
            \\ `--. _ _ __ ___  _ __ | | ___  | |_| | | |   | | | |_/ / \\ `--.  ___ _ ____   _____ _ __\s
             `--. \\ | '_ ` _ \\| '_ \\| |/ _ \\ |  _  | | |   | | |  __/   `--. \\/ _ \\ '__\\ \\ / / _ \\ '__|
            /\\__/ / | | | | | | |_) | |  __/ | | | | | |   | | | |     /\\__/ /  __/ |   \\ V /  __/ |  \s
            \\____/|_|_| |_| |_| .__/|_|\\___| \\_| |_/ \\_/   \\_/ \\_|     \\____/ \\___|_|    \\_/ \\___|_|  \s
                              | |                                                                     \s
                              |_|                                                                     \s
            """;

    public static void serve(Class<?> clazz) {

        log.info("\n{}", BANNER);

        final HttpEndpointResolver resolver = new HttpEndpointResolver();

        EndpointScanner scanner = new EndpointScanner(resolver);
        scanner.scan(clazz);

        try (Server server = new Server(resolver)){
            server.start();
        } catch (IOException e) {
            throw new ServerInitializationException(e.getMessage());
        }

    }
}
