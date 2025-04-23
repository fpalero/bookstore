package com.app.bookstore.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BookStoreHandlerExceptions {

    @ExceptionHandler({ PurchaseException.class, ClientException.class })
    public ResponseEntity<Object> handleGlobalException(Exception exception) {
        BookStoreException bookStoreException = (BookStoreException) exception;
        BookStoreError error = new BookStoreError();
        error.setErrorCode(bookStoreException.getErroCode());
        error.setMsg("Book Store exception thrown: " + exception.getLocalizedMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        BookStoreError error = new BookStoreError();
        error.setErrorCode(BookStoreErrorCodes.VALIDATION_ERROR.getErrorCode());
        error.setMsg("Book Store exception thrown: " + errors.toString());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

}
