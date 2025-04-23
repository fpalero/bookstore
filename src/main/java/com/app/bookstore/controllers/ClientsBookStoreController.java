package com.app.bookstore.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.bookstore.dtos.OrderDto;
import com.app.bookstore.dtos.PurchaseDto;
import com.app.bookstore.entities.ClientEntity;
import com.app.bookstore.entities.OrderEntity;
import com.app.bookstore.entities.PurchaseEntity;
import com.app.bookstore.exceptions.BookStoreError;
import com.app.bookstore.services.GetLoyaltyPointsService;
import com.app.bookstore.services.PurchaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/bookstore/clients")
@AllArgsConstructor
public class ClientsBookStoreController {
    private GetLoyaltyPointsService getLoyaltyPointsService;
    private PurchaseService purchaseService;
    private final ModelMapper bookStoreModelMapper;

    /**
     * Get loyalty points
     * 
     * @param clientId
     * @return
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results are ok", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request. Error Codes\n" + //
                    "      CLIENT_NOT_FOUND(100) - The client is not found", content = {
                    @Content(schema = @Schema(implementation = BookStoreError.class)) }),
            @ApiResponse(responseCode = "404", description = "resource not found", content = @Content) })
    @Operation(summary = "Get client loylty points", description = "Get client loyalty points")
    @GetMapping(path = "/{id}/loyalty", produces = "application/json")
    public ResponseEntity<Long> loyalty(@PathVariable(name = "id") Long clientId) {
        ClientEntity clientEntity = getLoyaltyPointsService.run(ClientEntity.builder().id(clientId).build());
        return new ResponseEntity<>(
                clientEntity.getLoyaltyPoints(),
                HttpStatus.OK);
    }

    /**
     * Purchase books
     * 
     * @param clientId
     * @param isbnList
     * @return
     */
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Results are ok", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request. Causes:" + //
                    "      Error Code 100: CLIENT_NOT_FOUND - The client is not found, \n" + //
                    "      Error Code 101: UNKNOWN_BOOK_TYPE - The book types are New Release, Regular and Old Editions, \n" + //
                    "      Error Code 102: INCORRECT_ORDER - The error appears when the order is not or empty, \n" + //
                    "      Error Code 103: NOT_ENOUGH_LOYALTY_POINTS - The client doesn't have enough point for receving a free book, \n" + //
                    "      Error Code 104: BOOK_NOT_FOUND - The book ISBN is not found;", content = {
                            @Content(schema = @Schema(implementation = BookStoreError.class)) }),
            @ApiResponse(responseCode = "404", description = "resource not found", content = @Content) })
    @Operation(summary = "Purchase books", description = "Calculates the total price of the order and the loyalty points earned by the client.\n" + //
            "      Pricing rules:\n" + //
            "      1 - New Releases: Full price (100% of the price).\n" + //
            "      2 - Regular: Full price, but a 10% discount applies if 3 or more books are purchased.\n" + //
            "      3 - Old Editions: 20% discount, with an additional 5% discount if 3 or more books are purchased.\n" + //
            "      4 - 1 loyalty point is awarded on every purchased book.\n" + //
            "      5 - The books that are in the list of free books are not included in the calculation. This applies to Regular and Old Editions.")
    @PostMapping(path = "/{id}/purchase", produces = "application/json")
    public ResponseEntity<PurchaseDto> purchase(
            @PathVariable("id") Long clientId,
            @Valid @RequestBody OrderDto order) {

        PurchaseEntity purchaseEntity = purchaseService.run(
                OrderEntity.builder()
                        .clientId(clientId)
                        .isbnList(order.getIsbnList())
                        .isbnFreeList(order.getIsbnFreeList())
                        .build());

        PurchaseDto purchaseDto = bookStoreModelMapper.map(
                purchaseEntity,
                PurchaseDto.class);

        return new ResponseEntity<>(purchaseDto, HttpStatus.OK);
    }

}
