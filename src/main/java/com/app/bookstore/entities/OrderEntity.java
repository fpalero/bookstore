package com.app.bookstore.entities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity implements StoreEntity {
    private Long clientId;
    private List<String> isbnList;
    private List<String> isbnFreeList;
}
