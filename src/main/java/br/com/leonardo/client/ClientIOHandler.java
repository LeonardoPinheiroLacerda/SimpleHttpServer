package br.com.leonardo.client;

import br.com.leonardo.client.input.ClientHttpRequestReader;
import br.com.leonardo.client.output.ClientHttpWriter;
import br.com.leonardo.client.output.factory.ClientHttpWriterFactory;
import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.parser.HttpRequestData;
import br.com.leonardo.parser.HttpRequestParser;
import br.com.leonardo.util.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Set;

@Slf4j
public record ClientIOHandler(Socket clientConnection) implements Runnable {

    @Override
    public void run() {
        try (
                OutputStream outputStream = clientConnection.getOutputStream();
                InputStream inputStream = clientConnection.getInputStream()
        ) {
            TraceIdUtil.initializeTraceId();
            handleRequest(inputStream, outputStream);
        } catch (IOException e) {
            log.error("Client IO Error", e);
        }

    }

    private void handleRequest(InputStream inputStream, OutputStream outputStream) throws IOException {
        final long start = System.nanoTime();

        final String clientRawRequest = ClientHttpRequestReader.readRequest(inputStream);

        if (clientRawRequest.length() <= 1) {
            return;
        }

        HttpRequestData requestData = HttpRequestParser.parseHttpRequest(clientRawRequest);
        dispatchResponse(outputStream, requestData);

        final long elapsedNanos = System.nanoTime() - start;
        log.info("Request {} handled in {} ms", requestData.requestLine(), elapsedNanos / 1_000_000);
    }

    private void dispatchResponse(OutputStream outputStream, HttpRequestData requestData) throws IOException {

        final RequestLine requestLine = requestData.requestLine();
        final Set<HttpHeader> headers = requestData.headers();
        final byte[] body = requestData.body();

        final ClientHttpWriter clientHttpWriter = ClientHttpWriterFactory.create(requestLine);

        try {
            final HttpResponse<?> response = clientHttpWriter
                    .generateResponse(
                            requestLine,
                            headers,
                            body
                    );

            log.trace("Writing response for request: {}", requestLine);
            final String rawResponse = clientHttpWriter.writeResponse(
                    outputStream,
                    response,
                    requestLine,
                    headers
            );

            if (ApplicationProperties.shouldLogResponses()) {
                log.info("Response: \n{}", rawResponse);
            }

            log.trace("Response written successfully for request: {}", requestLine);

        } catch (HttpException e) {
            ClientIOErrorHandler.dispatchHttpException(
                    outputStream,
                    clientHttpWriter,
                    requestLine,
                    headers,
                    e
            );
        } catch (Exception e) {
            ClientIOErrorHandler.dispatchException(
                    outputStream,
                    clientHttpWriter,
                    requestLine,
                    headers,
                    e
            );
        }
    }

}
