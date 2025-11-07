package br.com.leonardo.client.output;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.RequestLine;
import br.com.leonardo.http.response.HttpResponse;
import br.com.leonardo.util.ContentNegotiationUtil;
import br.com.leonardo.util.TraceIdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

@Slf4j
public class ClientHttpResponseWriter {

    public static void writeResponse(OutputStream outputStream, HttpResponse<?> response, RequestLine requestLine, Set<HttpHeader> headers, String body) throws IOException {

        final HttpHeader acceptHeader = ContentNegotiationUtil.resolveSupportedAcceptHeader(headers);

        byte[] responseBody;

        if(ContentNegotiationUtil.isStaticResourceRequest(requestLine.uri())) {
            responseBody = ContentNegotiationUtil.serializeStaticBody(requestLine.uri());
            ContentNegotiationUtil.setContentTypeAndContentLengthForStaticResources(responseBody, requestLine.uri(), response);
        } else {
            responseBody = ContentNegotiationUtil.serializePlainBody(response.getBody(), acceptHeader);
            ContentNegotiationUtil.setContentTypeAndContentLength(acceptHeader, responseBody, response);
        }


        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("%s %d %s\r\n"
                .formatted(requestLine.version(), response.getStatusCode().getCode(), response.getStatusCode().getText())
        );

        TraceIdUtil.addHeader(response.getHeaders());
        response
                .getHeaders()
                .forEach(stringBuilder::append);

        stringBuilder.append("\r\n");

        final String raw = stringBuilder.toString();

        if(ApplicationProperties.shouldLogResponses()){
            log.info("Response: \n{}", raw);
        }

        outputStream.write(raw.getBytes());
        for(byte b : responseBody){
            outputStream.write(b);
        }
    }

    public static void writeErrorResponse(OutputStream outputStream, HttpException httpException) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final String responseBody = objectMapper.writeValueAsString(httpException.responseBody());

        final String raw = "HTTP/1.1 %d %s\r\nContent-Type: application/json\r\nContent-Length: %d\r\n%s\r\n%s"
                .formatted(
                        httpException.getStatusCode().getCode(),
                        httpException.getStatusCode().getText(),
                        responseBody.getBytes().length,
                        TraceIdUtil.getHeader(),
                        responseBody
                );

        if (ApplicationProperties.shouldLogResponses()) {
            log.info("Error response: \n{}", raw);
        }

        outputStream.write(raw.getBytes());
    }

}
