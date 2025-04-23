package com.app.bookstore.entities;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseEntity implements StoreEntity {
    private ClientEntity client;
    private List<BookEntity> books;
    private Long loyaltyPoints;
    private Long totalPrice;
    private LocalDateTime purchaseDate;
}
