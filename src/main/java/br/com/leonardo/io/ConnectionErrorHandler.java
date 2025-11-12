package br.com.leonardo.io;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.enums.HttpStatusCode;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.io.output.HttpWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

@Slf4j
public class ConnectionErrorHandler {

    private ConnectionErrorHandler() {}

    public static void dispatchHttpException(OutputStream outputStream,
                                       HttpWriter httpWriter,
                                       RequestLine requestLine,
                                       Set<HttpHeader> headers,
                                       HttpException e) throws IOException {
        log.error("Something went wrong", e);
        httpWriter
                .writeResponse(
                        outputStream,
                        null,
                        requestLine,
                        headers,
                        e
                );
    }

    public static void dispatchException(OutputStream outputStream,
                                   HttpWriter httpWriter,
                                   RequestLine requestLine,
                                   Set<HttpHeader> headers,
                                   Exception e) throws IOException {
        log.error("Something unexpected went wrong", e);
        httpWriter.writeResponse(
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
