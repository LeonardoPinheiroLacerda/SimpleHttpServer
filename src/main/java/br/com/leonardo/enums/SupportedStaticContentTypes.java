package br.com.leonardo.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum SupportedStaticContentTypes {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/javascript"),
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    TXT("txt", "text/plain"),
    CSV("csv", "text/csv"),

    // --- Imagens ---
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    GIF("gif", "image/gif"),
    SVG("svg", "image/svg+xml"),
    WEBP("webp", "image/webp"),
    ICO("ico", "image/x-icon"),

    // --- Fontes ---
    TTF("ttf", "font/ttf"),
    WOFF("woff", "font/woff"),
    WOFF2("woff2", "font/woff2"),

    // --- Mídia ---
    MP4("mp4", "video/mp4"),
    MP3("mp3", "audio/mpeg"),

    // --- Arquivos binários ---
    PDF("pdf", "application/pdf"),
    ZIP("zip", "application/zip"),
    GZIP("gz", "application/gzip"),
    WASM("wasm", "application/wasm");

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
