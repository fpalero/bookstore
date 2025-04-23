package com.app.bookstore.entities;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PurchaseEntity extends StoreEntity {
    private ClientEntity client;
    private List<BookEntity> books;
    private Long loyaltyPoints;
    private Long totalPrice;
    private LocalDateTime purchaseDate;
}
