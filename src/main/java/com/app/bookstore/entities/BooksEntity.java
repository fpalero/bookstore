package com.app.bookstore.entities;

import java.util.List;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class BooksEntity extends StoreEntity {
    private List<BookEntity> books;
}
