package br.com.leonardo.parser;

public class RequestBodyParser {

    private RequestBodyParser() {}

    private static final String DOUBLE_CRLF = "\r\n\r\n";

    public static byte[] parseRequestBody(String rawRequest) {

        final String[] chunks = rawRequest.split(DOUBLE_CRLF);

        if (chunks.length == 1) {
            return new byte[0];
        }

        return chunks[1].getBytes();
    }

}
