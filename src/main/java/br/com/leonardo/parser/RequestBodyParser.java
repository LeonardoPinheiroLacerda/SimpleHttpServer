package br.com.leonardo.parser;

public class RequestBodyParser {

    private final static String doubleCrlf = "\r\n\r\n";

    protected static byte[] parseRequestBody(String rawRequest) {

        final String[] chunks = rawRequest.split(doubleCrlf);

        if (chunks.length == 1) {
            return null;
        }

        return chunks[1].getBytes();
    }

}
