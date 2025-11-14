package br.com.leonardo.io.output;

import br.com.leonardo.enums.ContentTypeEnum;
import br.com.leonardo.enums.HttpHeaderEnum;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.io.output.util.ContentTypeNegotiation;
import br.com.leonardo.observability.TraceIdLifeCycleHandler;
import br.com.leonardo.parser.factory.model.HttpRequestData;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public interface HttpWriter {

    ContentTypeNegotiation contentTypeNegotiation = new ContentTypeNegotiation();

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
                    .header(HttpHeaderEnum.CONTENT_TYPE.getName(), ContentTypeEnum.APPLICATION_JSON.getType())
                    .header(HttpHeaderEnum.CONTENT_LENGTH.getName(), bodyBytes.length)
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
        final HttpHeader acceptHeader = contentTypeNegotiation.resolveSupportedAcceptHeader(headers);

        final HttpResponse<Object> response = HttpResponse.builder()
                .statusCode(exception.getStatusCode())
                .build();

        byte[] bodyBytes = contentTypeNegotiation.serializePlainBody(exception.responseBody(), acceptHeader);
        contentTypeNegotiation.setContentTypeAndContentLength(acceptHeader, bodyBytes, response);

        return bodyBytes;
    }

    HttpResponse<?> generateResponse(HttpRequestData requestData) throws HttpException;

    byte[] getBody(RequestLine requestLine,
                   Set<HttpHeader> headers,
                   HttpResponse<?> response) throws IOException;
}
