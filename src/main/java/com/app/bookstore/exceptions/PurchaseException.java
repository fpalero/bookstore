package com.app.bookstore.exceptions;

public class PurchaseException extends BookStoreException {
    private static final long serialVersionUID = 1L;

    public PurchaseException(String message, int erroCode) {
        super(message, erroCode);
    }

    public PurchaseException(String message, int erroCode, Throwable cause) {
        super(message, erroCode, cause);
    }

}
