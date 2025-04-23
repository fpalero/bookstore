package com.app.bookstore.entities;

import java.util.List;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class BooksEntity implements StoreEntity {
    private List<BookEntity> books;
}
