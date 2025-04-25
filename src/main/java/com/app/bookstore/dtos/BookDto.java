package com.app.bookstore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Book.
 * This class is used to transfer book data between different layers of the
 * application.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private String title;
    @Schema(description = "The quantity of books in stock. Whne the value is 0, the book is not available for sale.")
    private Integer quantity;
    @Schema(description = "The books are grouped on 3 types: New Release, Regular and Old Editions.", example = "REGULAR")
    private String type;
    private Long price;
    private String author;
    private String publisher;

    @Schema(description = "The ISBN identify a book and it is used for purchasing.", example = "978-3-16-148410-0")
    private String isbn;
    private String description;
    private Integer publicationYear;
}
