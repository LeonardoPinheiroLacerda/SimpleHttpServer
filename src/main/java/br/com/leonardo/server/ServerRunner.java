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
            __________             ___________             ________
            ___  ____/_____ _________  /___  / ___________ ___  __/
            __  /_   _  __ `/_  ___/  __/_  /  _  _ \\  __ `/_  /_ \s
            _  __/   / /_/ /_(__  )/ /_ _  /___/  __/ /_/ /_  __/ \s
            /_/      \\__,_/ /____/ \\__/ /_____/\\___/\\__,_/ /_/    \s
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
