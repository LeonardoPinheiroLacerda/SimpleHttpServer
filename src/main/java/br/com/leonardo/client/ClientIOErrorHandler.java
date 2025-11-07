package br.com.leonardo.client;

import br.com.leonardo.client.output.ClientHttpWriter;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

@Slf4j
public class ClientIOErrorHandler {

    public static void dispatchHttpException(OutputStream outputStream,
                                       ClientHttpWriter clientHttpWriter,
                                       RequestLine requestLine,
                                       Set<HttpHeader> headers,
                                       HttpException e) throws IOException {
        log.error("Something went wrong", e);
        clientHttpWriter
                .writeResponse(
                        outputStream,
                        null,
                        requestLine,
                        headers,
                        e
                );
    }

    public static void dispatchException(OutputStream outputStream,
                                   ClientHttpWriter clientHttpWriter,
                                   RequestLine requestLine,
                                   Set<HttpHeader> headers,
                                   Exception e) throws IOException {
        log.error("Something unexpected went wrong", e);
        clientHttpWriter.writeResponse(
                outputStream,
                null,
                requestLine,
                headers,
                new HttpException(
                        "Something unexpected went wrong",
                        HttpStatusCode.INTERNAL_SERVER_ERROR,
                        null
                )
        );
    }


}
