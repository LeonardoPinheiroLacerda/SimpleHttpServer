package br.com.leonardo.http;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum SupportedStaticContentTypes {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    GIF("gif", "image/gif"),
    SVG("svg", "image/svg+xml"),
    TTF("ttf", "font/ttf"),
    WOFF("woff", "font/woff"),
    WOFF2("woff2", "font/woff2"),
    PDF("pdf", "application/pdf"),
    MP4("mp4", "video/mp4"),
    MP3("mp3", "audio/mpeg"),
    WEBP("webp", "image/webp"),
    ICO("ico", "image/x-icon"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    PLAIN("txt", "text/plain");


    private final String extension;
    private final String mediaType;

    public static String getMediaType(String extension) {
        return Arrays
                .stream(values())
                .filter(type -> extension.equalsIgnoreCase(type.extension))
                .map(type -> type.mediaType)
                .findFirst()
                .orElse(null);
    }
}
