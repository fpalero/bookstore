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
    @Schema(description = "The list of books is needed to make the purchase.")
    private List<String> isbnList;
    private List<String> isbnFreeList;
}
