package com.app.bookstore.exceptions;

public class BookStoreException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private int erroCode;

    public BookStoreException(String message, int erroCode) {
        super(message);
        this.erroCode = erroCode;
    }

    public BookStoreException(String message, int erroCode, Throwable cause) {
        super(message, cause);
        this.erroCode = erroCode;
    }

    public int getErroCode() {
        return erroCode;
    }
    
}
