package br.com.leonardo.io;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.context.resolver.ResolversContextHolder;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.io.input.HttpRequestReader;
import br.com.leonardo.io.output.HttpWriter;
import br.com.leonardo.io.output.factory.HttpWriterFactory;
import br.com.leonardo.io.output.util.ContentTypeNegotiation;
import br.com.leonardo.observability.TraceIdLifeCycleHandler;
import br.com.leonardo.parser.factory.HttpRequestFactory;
import br.com.leonardo.parser.factory.model.HttpRequestData;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

@Slf4j
public record ConnectionIOHandler(
        Socket clientConnection,
        ResolversContextHolder resolvers
) implements Runnable {

    @Override
    public void run() {
        try (
                OutputStream outputStream = clientConnection.getOutputStream();
                InputStream inputStream = clientConnection.getInputStream()
        ) {
            TraceIdLifeCycleHandler.initializeTraceId();
            handleRequest(inputStream, outputStream);
        } catch (IOException e) {
            log.error("Client IO Error", e);
        }

    }

    private void handleRequest(InputStream inputStream, OutputStream outputStream) throws IOException {
        final long start = System.nanoTime();

        final String clientRawRequest = HttpRequestReader.readRequest(inputStream);

        if (clientRawRequest.length() <= 1) {
            return;
        }

        HttpRequestData requestData = HttpRequestFactory.fromRawRequest(clientRawRequest);
        dispatchResponse(outputStream, requestData);

        final long elapsedNanos = System.nanoTime() - start;
        log.info("Request {} handled in {} ms", requestData.requestLine(), elapsedNanos / 1_000_000);
    }

    private void dispatchResponse(OutputStream outputStream, HttpRequestData requestData) throws IOException {

        final RequestLine requestLine = requestData.requestLine();
        final Set<HttpHeader> headers = requestData.headers();

        final ContentTypeNegotiation contentTypeNegotiation = new ContentTypeNegotiation();
        final HttpWriter httpWriter = HttpWriterFactory.create(contentTypeNegotiation, requestLine, resolvers.getHttpEndpointResolver());

        try {
            final HttpResponse<?> response = httpWriter.generateResponse(requestData);

            log.trace("Writing response for request: {}", requestLine);
            final String rawResponse = httpWriter.writeResponse(
                    outputStream,
                    response,
                    requestLine,
                    headers
            );

            if (ApplicationProperties.shouldLogResponses()) {
                log.info("Response: \n{}", rawResponse);
            }

            log.trace("Response written successfully for request: {}", requestLine);

        } catch (Exception e) {
            ConnectionErrorHandler.dispatchException(outputStream, httpWriter, requestData, resolvers.getHttpEndpointResolver(), resolvers.getHttpExceptionHandlerResolver(), e);
        }
    }

}
