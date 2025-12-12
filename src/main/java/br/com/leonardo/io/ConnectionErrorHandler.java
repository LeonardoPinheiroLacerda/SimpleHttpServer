package br.com.leonardo.io;

import br.com.leonardo.context.resolver.HttpEndpointResolver;
import br.com.leonardo.context.resolver.HttpExceptionHandlerResolver;
import br.com.leonardo.exception.handler.HttpExceptionHandler;
import br.com.leonardo.exception.handler.impl.InternalServerErrorHttpExceptionHandler;
import br.com.leonardo.exception.handler.model.ProblemDetails;
import br.com.leonardo.http.request.HttpRequest;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.io.output.HttpWriter;
import br.com.leonardo.observability.TraceIdLifeCycleHandler;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import br.com.leonardo.router.core.HttpEndpoint;
import br.com.leonardo.router.extractor.HeaderExtractor;
import br.com.leonardo.router.extractor.PathVariableExtractor;
import br.com.leonardo.router.extractor.QueryParameterExtractor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

@Slf4j
public class ConnectionErrorHandler {

    private ConnectionErrorHandler() {}

    public static void dispatchException(OutputStream outputStream,
                                         HttpWriter httpWriter,
                                         HttpRequestData requestData,
                                         HttpEndpointResolver endpointResolver,
                                         HttpExceptionHandlerResolver exceptionResolver,
                                         Exception e
    ) throws IOException {
        log.error("Something unexpected went wrong", e);

        final HttpEndpoint<?, ?> httpEndpoint = endpointResolver.get(requestData)
                .orElse(null);

        HttpExceptionHandler<?, ?> httpExceptionHandler = exceptionResolver
                .getRecursive(e.getClass())
                .orElse(null);

        final HttpRequest<?> request = new HttpRequest<>(
                requestData.requestLine(),
                HeaderExtractor.extract(requestData.headers()),
                requestData.body(),
                PathVariableExtractor.extract(requestData.requestLine(), httpEndpoint),
                QueryParameterExtractor.extract(requestData.requestLine()),
                new HashMap<>()
        );

        final ProblemDetails problemDetails = new ProblemDetails(request, TraceIdLifeCycleHandler.getTraceId());

        HttpResponse<?> response;

        if (httpExceptionHandler == null) {
            log.error("No HttpExceptionHandler found for exception type: {}.", e.getClass().getName());
            httpExceptionHandler = new InternalServerErrorHttpExceptionHandler();
        }

        response = callHttpExceptionHandler(httpExceptionHandler, problemDetails, e);

        httpWriter.writeResponse(outputStream, response, requestData.requestLine(), requestData.headers());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> HttpResponse<?> callHttpExceptionHandler(
        HttpExceptionHandler<T, ?> handler,
        ProblemDetails problemDetails,
        Exception originalException
    ) {
        return handler.handle(problemDetails, (T) originalException);
    }

}
