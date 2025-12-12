package br.com.leonardo.server;

import br.com.leonardo.context.resolver.ResolversContextHolder;
import br.com.leonardo.context.scanner.Scanners;
import br.com.leonardo.exception.ServerInitializationException;
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

        final ResolversContextHolder resolvers = Scanners.scan(clazz);

        try (Server server = new Server(resolvers)){
            server.start();
        } catch (IOException e) {
            throw new ServerInitializationException(e.getMessage());
        }

    }
}
