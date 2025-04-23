package com.app.bookstore.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookStoreError {
    private int errorCode;
    private String msg;
}
