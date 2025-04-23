package com.app.bookstore.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.bookstore.dtos.BooksDto;
import com.app.bookstore.entities.EmptyEnitity;
import com.app.bookstore.services.GetBooksService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/bookstore/books")
@AllArgsConstructor
public class BooksBookStoreController {
    private final GetBooksService getBooksService;
    private final ModelMapper bookStoreModelMapper;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results are ok", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BooksDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "resource not found", content = @Content) })
    @Operation(summary = "Get all books", description = "Get all books from the Book Store that are available for sale")
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<BooksDto> books() {

        BooksDto books = bookStoreModelMapper.map(
                getBooksService.run(new EmptyEnitity()),
                BooksDto.class);

        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}
