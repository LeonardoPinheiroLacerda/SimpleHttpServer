package br.com.leonardo.exception;

import br.com.leonardo.http.HttpStatusCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class HttpException extends RuntimeException {

    private final String message;
    private final HttpStatusCode statusCode;
    private final long timestamp;
    private final String path;

    public HttpException(String message,
                         HttpStatusCode statusCode,
                         String path) {
        super(message);

        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = System.currentTimeMillis();
        this.path = path;
    }

    public Map<String, Object> responseBody() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", this.message);
        responseBody.put("status", this.statusCode.getCode());
        responseBody.put("timestamp", this.timestamp);
        if(path != null)
            responseBody.put("path", this.path);
        return responseBody;
    }
}
