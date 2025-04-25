package com.app.bookstore.dtos;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @NotNull
    @NotEmpty
    @Schema(description = "The list of books is needed to make the purchase. An ISBN can appear multiple times in the list.",
            example = "[\"9780061120084\", \"9780061120084\", \"9781503280786\"]")
    private List<String> purchasedBook;
    @Schema(description = "The ISBN of books that are free should appear on the purchasedBook list",
            example = "[\"9780061120084\"]")
    private List<String> freeBooks;
}
