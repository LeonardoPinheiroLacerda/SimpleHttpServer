package br.com.leonardo.exception;

public class ServerInitializationException extends RuntimeException {
    public ServerInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
    public ServerInitializationException(String message) {
        super(message);
    }
}
