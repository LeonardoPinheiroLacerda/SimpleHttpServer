package br.com.leonardo.io.input;

import br.com.leonardo.config.ApplicationProperties;
import br.com.leonardo.exception.HttpException;
import br.com.leonardo.enums.HttpStatusCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class HttpRequestReader {

    private HttpRequestReader() {}

    public static String readRequest(InputStream inputStream) {
        try {
            StringBuilder stringBuilder = new StringBuilder();

            do {
                stringBuilder.append((char) inputStream.read());
            } while (inputStream.available() > 0);

            final String raw = stringBuilder.toString();

            if(ApplicationProperties.shouldLogRequests() && raw.length() > 1){
                log.info("Request: \n{}", raw);
            }

            return raw;
        }catch (IOException e) {
            throw new HttpException("Something went wrong while your request were read", HttpStatusCode.INTERNAL_SERVER_ERROR, null);
        }
    }

}
