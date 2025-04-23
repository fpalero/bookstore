package com.app.bookstore.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDto {
    private ClientDto client;
    private List<BookDto> books;
    private Long loyaltyPoints;
    private Long totalPrice;
    private String purchaseDate;
}
