package com.app.bookstore.exceptions;

public class ClientException extends BookStoreException {
    private static final long serialVersionUID = 1L;

    public ClientException(String message, int erroCode) {
        super(message, erroCode);
    }

    public ClientException(String message, int erroCode, Throwable cause) {
        super(message, erroCode, cause);
    }
}
