package com.app.bookstore.exceptions;

public enum BookStoreErrorCodes {
    CLIENT_NOT_FOUND(100),
    UNKNOWN_BOOK_TYPE(101), 
    INCORRECT_ORDER(102), 
    NOT_ENOUGH_LOYALTY_POINTS(103), 
    BOOK_NOT_FOUND(104), 
    VALIDATION_ERROR(105),;

    private final int code;
    BookStoreErrorCodes(int i) {
        this.code = i;
    }

    public int getErrorCode() {
        return code;
    }
}
