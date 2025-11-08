package br.com.leonardo.annotation.scanner;

import br.com.leonardo.annotation.Endpoint;
import br.com.leonardo.exception.ServerInitializationException;
import br.com.leonardo.router.core.HttpEndpoint;
import br.com.leonardo.router.core.HttpEndpointResolver;
import br.com.leonardo.http.middleware.Middleware;
import br.com.leonardo.observability.nodetree.Node;
import br.com.leonardo.observability.nodetree.TreeNodeLogger;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public record EndpointScanner(
        HttpEndpointResolver resolver
) {

    public void scan(Class<?> clazz) {
        final String pack = clazz.getPackage().getName();
        final Reflections reflections = new Reflections(pack);

        log.info("Scanning project");
        final Node root = new Node("Scanning package " + pack);

        reflections
                .getTypesAnnotatedWith(Endpoint.class)
                .forEach(endpoint -> {
                    final Endpoint annotation = endpoint.getAnnotation(Endpoint.class);


                    final Node endpointNode = new Node(endpoint.getName());
                    final Node uriNode = new Node("URL:        " + annotation.url());
                    final Node methodNode = new Node("Method:     " + annotation.method().name());
                    endpointNode.addChild(uriNode);
                    endpointNode.addChild(methodNode);

                    try {
                        final HttpEndpoint<?, ?> httpEndpoint = (HttpEndpoint<?, ?>) endpoint
                                .getDeclaredConstructor()
                                .newInstance();

                        httpEndpoint.setUri(annotation.url());
                        httpEndpoint.setMethod(annotation.method());

                        for (Class<? extends Middleware> middlewareClass : annotation.middlewares()) {

                            final Middleware middleware = middlewareClass
                                    .getDeclaredConstructor()
                                    .newInstance();

                            httpEndpoint.addMiddleware(middleware);

                            final Node middlewareNode = new Node("Middleware: " + middleware.getClass().getName());
                            endpointNode.addChild(middlewareNode);

                        }

                        resolver.add(httpEndpoint);
                        root.addChild(endpointNode);

                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new ServerInitializationException("It was not possible to initialize server.", e);
                    }
                });

        new TreeNodeLogger(log)
                .logTree(root);

    }

}
