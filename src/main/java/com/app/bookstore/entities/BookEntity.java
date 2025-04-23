package com.app.bookstore.entities;

import com.app.bookstore.types.BookType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity class representing a book in the bookstore system.
 * This class is used to map book data to the database.
 */
@Entity
@Table(name = "books")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BookEntity implements StoreEntity {
    @Id
    protected Long id;
    private String title;
    @Enumerated(EnumType.STRING)
    private BookType type;
    private Long price;
    private String author;
    private String publisher;
    private String isbn;
    private String description;
    @Column(name = "publication_year")
    private Integer publicationYear;
    
    @Builder.Default
    private boolean sold = false;
}
