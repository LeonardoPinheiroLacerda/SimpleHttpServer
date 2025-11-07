package br.com.leonardo.util;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.http.HttpHeader;
import br.com.leonardo.http.SupportedStaticContentTypes;
import br.com.leonardo.http.response.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ContentNegotiationUtil {

    private ContentNegotiationUtil() {}

    public static HttpHeader resolveSupportedAcceptHeader(Set<HttpHeader> headers) {
        return headers.stream()
                .filter(header -> "Accept".equalsIgnoreCase(header.name()))
                .findFirst()
                .orElse(new HttpHeader("Accept", "application/json"));
    }

    public static boolean isStaticResourceRequest(String uri) {
        final String[] chunks = uri.split("\\.");
        if(chunks.length == 0) {
            return false;
        }
        final String extension = chunks[chunks.length - 1];
        return SupportedStaticContentTypes.getMediaType(extension) != null;
    }

    public static void setContentTypeAndContentLength(HttpHeader acceptHeader, byte[] body, HttpResponse<?> response) {
        switch (acceptHeader.value()) {
            case "application/xml" -> response.addHeader("Content-Type", "application/xml");
            case "text/plain" -> response.addHeader("Content-Type", "text/plain");
            default -> response.addHeader("Content-Type", "application/json");
        }
        response.addHeader("Content-Length", body.length);
    }

    public static void setContentTypeAndContentLengthForStaticResources(byte[] body, String uri, HttpResponse<?> response) {
        final String[] chunks = uri.split("\\.");
        final String extension = chunks[chunks.length - 1];
        response.addHeader("Content-Type", SupportedStaticContentTypes.getMediaType(extension));
        response.addHeader("Content-Length", body.length);
    }

    public static byte[] serializePlainBody(Object body, HttpHeader acceptHeader) throws JsonProcessingException {
        return switch (acceptHeader.value()) {
            case "application/xml" -> {
                final XmlMapper xmlMapper = new XmlMapper();
                yield xmlMapper.writeValueAsString(body).getBytes();
            }
            case "text/plain" -> body.toString().getBytes();
            default -> {
                final ObjectMapper objectMapper = new ObjectMapper();
                yield objectMapper.writeValueAsString(body).getBytes();
            }
        };
    }

    public static Boolean existsStatic(String uri) {
        final String path = ApplicationProperties.getStaticContentPath() + uri;
        final ClassLoader classLoader = ContentNegotiationUtil.class.getClassLoader();
        return classLoader.getResource(path) != null;
    }

    public static byte[] serializeStaticBody(String uri) throws IOException {

        final String path = ApplicationProperties.getStaticContentPath() + uri;
        final ClassLoader classLoader = ContentNegotiationUtil.class.getClassLoader();

        try(InputStream inputStream = classLoader.getResourceAsStream(path)) {
            if(inputStream == null) {
                throw new IOException("Resource not found: " + path);
            }

            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new IOException("Error reading resource: " + path, e);
        }

    }
}