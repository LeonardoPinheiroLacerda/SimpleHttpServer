package br.com.leonardo.io.output;

import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.util.ContentNegotiationUtil;
import br.com.leonardo.observability.TraceIdLifeCycleHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public interface HttpWriter {

    default String writeResponse(OutputStream outputStream,
                                 HttpResponse<?> response,
                                 RequestLine requestLine,
                                 Set<HttpHeader> headers
    ) throws IOException {
        return writeResponse(outputStream, response, requestLine, headers, null);
    }

    default String writeResponse(OutputStream outputStream,
                                 HttpResponse<?> response,
                                 RequestLine requestLine,
                                 Set<HttpHeader> headers,
                                 HttpException exception
    ) throws IOException {

        byte[] bodyBytes;

        //Getting success/error body based on exception
        if (exception != null) {
            headers.clear();

            bodyBytes = getErrorBody(requestLine, headers, exception);
            response = HttpResponse
                    .builder()
                    //Deve acompanhar o Accept header
                    .header("Content-Type", "application/json")
                    .header("Content-Length", bodyBytes.length)
                    .statusCode(exception.getStatusCode())
                    .build();

        } else {
            bodyBytes = getBody(requestLine, headers, response);
        }

        final StringBuilder stringBuilder = new StringBuilder();

        //Request line
        stringBuilder.append(requestLine.version());
        stringBuilder.append(" ");
        stringBuilder.append(response.getStatusCode().getCode());
        stringBuilder.append(" ");
        stringBuilder.append(response.getStatusCode().getText());
        stringBuilder.append("\r\n");

        //Headers
        TraceIdLifeCycleHandler.addHeader(response.getHeaders());
        response
                .getHeaders()
                .forEach(header -> {
                    stringBuilder.append(header.name());
                    stringBuilder.append(": ");
                    stringBuilder.append(header.value());
                    stringBuilder.append("\r\n");
                });

        stringBuilder.append("\r\n");

        //Write Request Line and Headers
        outputStream.write(stringBuilder.toString().getBytes());

        //Write Body
        outputStream.write(bodyBytes);

        if (this instanceof ApiHttpResponseWriter) {
            stringBuilder.append(new String(bodyBytes));
        } else {
            stringBuilder.append("[binary data]");
        }

        return stringBuilder.toString();
    }

    default byte[] getErrorBody(RequestLine requestLine,
                                Set<HttpHeader> headers,
                                HttpException exception) throws IOException {
        final HttpHeader acceptHeader = ContentNegotiationUtil.resolveSupportedAcceptHeader(headers);

        final HttpResponse<Object> response = HttpResponse.builder()
                .statusCode(exception.getStatusCode())
                .build();

        byte[] bodyBytes = ContentNegotiationUtil.serializePlainBody(exception.responseBody(), acceptHeader);
        ContentNegotiationUtil.setContentTypeAndContentLength(acceptHeader, bodyBytes, response);

        return bodyBytes;
    }

    HttpResponse<?> generateResponse(
            RequestLine requestLine,
            Set<HttpHeader> headers,
            byte[] body) throws HttpException;

    byte[] getBody(RequestLine requestLine,
                   Set<HttpHeader> headers,
                   HttpResponse<?> response) throws IOException;
}
