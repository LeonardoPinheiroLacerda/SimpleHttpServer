package br.com.leonardo.io.output.util;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.enums.ContentTypeEnum;
import br.com.leonardo.enums.HttpHeaderEnum;
import br.com.leonardo.enums.SupportedStaticContentTypes;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.response.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ContentTypeNegotiation {

    public HttpHeader resolveSupportedAcceptHeader(Set<HttpHeader> headers) {
        return headers.stream()
                .filter(header -> HttpHeaderEnum.ACCEPT.getName().equalsIgnoreCase(header.name()))
                .findFirst()
                .orElse(new HttpHeader(HttpHeaderEnum.ACCEPT.getName(), ContentTypeEnum.APPLICATION_JSON.getType()));
    }

    public boolean isStaticResourceRequest(String uri) {
        final String[] chunks = uri.split("\\.");
        if (chunks.length == 0) {
            return false;
        }
        final String extension = chunks[chunks.length - 1];
        return SupportedStaticContentTypes.getMediaType(extension) != null;
    }

    public void setContentTypeAndContentLength(HttpHeader acceptHeader, byte[] body, HttpResponse<?> response) {
        final String acceptHeaderValue = acceptHeader.value();

        if (ContentTypeEnum.APPLICATION_XML.getType().equals(acceptHeaderValue)) {
            response.addHeader(HttpHeaderEnum.CONTENT_TYPE.getName(), ContentTypeEnum.APPLICATION_XML.getType());
        } else if (ContentTypeEnum.TEXT_PLAIN.getType().equals(acceptHeaderValue)) {
            response.addHeader(HttpHeaderEnum.CONTENT_TYPE.getName(), ContentTypeEnum.TEXT_PLAIN.getType());
        } else {
            response.addHeader(HttpHeaderEnum.CONTENT_TYPE.getName(), ContentTypeEnum.APPLICATION_JSON.getType());
        }

        response.addHeader(HttpHeaderEnum.CONTENT_LENGTH.getName(), body.length);
    }

    public void setContentTypeAndContentLengthForStaticResources(byte[] body, String uri, HttpResponse<?> response) {
        final String[] chunks = uri.split("\\.");
        final String extension = chunks[chunks.length - 1];
        response.addHeader(HttpHeaderEnum.CONTENT_TYPE.getName(), SupportedStaticContentTypes.getMediaType(extension));
        response.addHeader(HttpHeaderEnum.CONTENT_LENGTH.getName(), body.length);
    }

    public byte[] serializePlainBody(Object body, HttpHeader acceptHeader) throws JsonProcessingException {
        final String acceptHeaderValue = acceptHeader.value();
        if (ContentTypeEnum.APPLICATION_XML.getType().equals(acceptHeaderValue)) {
            final XmlMapper xmlMapper = new XmlMapper();
            return xmlMapper.writeValueAsString(body).getBytes();
        } else if (ContentTypeEnum.TEXT_PLAIN.getType().equals(acceptHeaderValue)) {
            return body.toString().getBytes();
        } else {
            final ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(body).getBytes();
        }
    }

    public boolean existsStatic(String uri) {
        final String path = ApplicationProperties.getStaticContentPath() + uri;
        final ClassLoader classLoader = ContentTypeNegotiation.class.getClassLoader();
        return classLoader.getResource(path) != null;
    }

    public byte[] serializeStaticBody(String uri) throws IOException {

        final String path = ApplicationProperties.getStaticContentPath() + uri;
        final ClassLoader classLoader = ContentTypeNegotiation.class.getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + path);
            }

            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Error reading resource: " + path, e);
        }

    }
}
