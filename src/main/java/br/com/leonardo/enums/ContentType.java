package br.com.leonardo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContentType {

    // --- Textos ---
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_CSV("text/csv"),
    TEXT_JAVASCRIPT("text/javascript"),

    // --- Aplicações ---
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    APPLICATION_XHTML_XML("application/xhtml+xml"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_ZIP("application/zip"),
    APPLICATION_GZIP("application/gzip"),

    // --- Imagens ---
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_SVG_XML("image/svg+xml"),

    // --- Áudio e Vídeo ---
    AUDIO_MPEG("audio/mpeg"),
    VIDEO_MP4("video/mp4"),
    VIDEO_WEBM("video/webm"),

    // --- Outros ---
    MULTIPART_FORM_DATA("multipart/form-data");

    private final String type;
}
