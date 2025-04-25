package com.app.bookstore.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.app.bookstore.entities.BookEntity;
import com.app.bookstore.entities.ClientEntity;
import com.app.bookstore.entities.OrderEntity;
import com.app.bookstore.entities.PurchaseEntity;
import com.app.bookstore.exceptions.BookStoreErrorCodes;
import com.app.bookstore.exceptions.PurchaseException;
import com.app.bookstore.repositories.BooksRepository;
import com.app.bookstore.repositories.ClientsRepository;
import com.app.bookstore.types.BookType;

@Service
public class PurchaseService implements StoreService<OrderEntity> {

    ClientsRepository clientsRepository;
    BooksRepository booksRepository;

    public PurchaseService(ClientsRepository clientsRepository, BooksRepository booksRepository) {
        this.clientsRepository = clientsRepository;
        this.booksRepository = booksRepository;
    }

    /**
     * Calculates the total price of the order and the loyalty points earned by the
     * client.
     * Pricing rules:
     * - New Releases: Full price (100% of the price).
     * - Regular: Full price, but a 10% discount applies if 3 or more books are
     * purchased.
     * - Old Editions: 20% discount, with an additional 5% discount if 3 or more
     * books are purchased.
     * - 1 loyalty point is awarded on every purchased book.
     * - The books that are in the list of free books are not included in the
     * calculation. This applies to Regular and Old Editions.
     * 
     * @param books     The list of books to calculate the price and loyalty points
     *                  for.
     * @param freeBooks The list of free book isbn to exclude from the calculation.
     * @return A PurchaseEntity containing the calculated total price and loyalty
     *         points.
     */
    public PurchaseEntity calculateOrderDetails(List<BookEntity> books, List<String> pruchasedBooks, List<String> freeIsbnBooks) {
        final long[] totalPrice = { 0L };
        final long[] loyaltyPoints = { 0L };


        if (books != null) {
            books.forEach(book -> {

                long bookPrice = book.getPrice();
                switch (book.getType()) {
                    case BookType.NEW_RELEASE:
                        long totalBooks = getTotalBooks(pruchasedBooks, book);
                        totalPrice[0] += bookPrice * totalBooks;
                        loyaltyPoints[0]+= totalBooks;
                        break;
                    case BookType.REGULAR:
                        long totalRegularBooks = getTotalBooks(pruchasedBooks, freeIsbnBooks, book);
                        if (pruchasedBooks.size() >= 3) {
                            totalPrice[0] += bookPrice * 0.9 * totalRegularBooks; // 10% discount
                        } else {
                            totalPrice[0] += bookPrice * totalRegularBooks;
                        }
                        loyaltyPoints[0]+= totalRegularBooks;
                        break;
                    case BookType.OLD_EDITIONS:
                        long totalOldBooks = getTotalBooks(pruchasedBooks, freeIsbnBooks, book);
                        if (pruchasedBooks.size() >= 3) {
                            totalPrice[0] += bookPrice * 0.75 * totalOldBooks; // 20% + 5% discount
                        } else {
                            totalPrice[0] += bookPrice * 0.8 * totalOldBooks; // 20% discount
                        }
                        loyaltyPoints[0]+=totalOldBooks;
                        break;
                    default:
                        throw new PurchaseException("Unknown book type: " + book.getType(),
                                BookStoreErrorCodes.UNKNOWN_BOOK_TYPE.getErrorCode());
                }
            });
        }

        return PurchaseEntity.builder()
                .loyaltyPoints(loyaltyPoints[0])
                .totalPrice(totalPrice[0])
                .books(books)
                .purchaseDate(LocalDateTime.now())
                .build();
    }

    @Override
    public PurchaseEntity run(OrderEntity order) throws PurchaseException {

        if (order == null) {
            throw new PurchaseException("Order cannot be null", BookStoreErrorCodes.INCORRECT_ORDER.getErrorCode());
        }

        ClientEntity client = getClient(order);

        List<BookEntity> books = getBooks(order);

        checkLoyaltyPoints(order.getFreeBooks(), client);

        PurchaseEntity purchase = calculateOrderDetails(books, order.getPurchasedBook() ,order.getFreeBooks());
        purchase.setClient(client);

        long usedLoyaltyPoints = getUsedLoyaltyPoints(books, order.getFreeBooks());
        updateClientLoyaltyPoints(client, purchase.getLoyaltyPoints(), usedLoyaltyPoints);

        updateBooksSoldStatus(books , order.getPurchasedBook());

        return purchase;
    }

    private Long getUsedLoyaltyPoints(List<BookEntity> books, List<String> freeBooks) {
        List<String> freeBooksList = List.copyOf(freeBooks);

        if (freeBooks == null || freeBooks.isEmpty()) {
            return 0L;

        }
        // Calculate the number of loyalty points used for the books purchased.
        // It is multiplied * 10 because it is necessary 10 loyalty point for one free book.
        return books.stream()
                .map(book -> {
                    if (freeBooksList.contains(book.getIsbn()) && book.getType() != BookType.NEW_RELEASE) {
                        return getTotalBooks(freeBooksList, book);
                    }
                    return 0L;
                })    
                .reduce(0L, Long::sum) * 10;
    }

    private void updateBooksSoldStatus(List<BookEntity> books, List<String> isbnList) {
        books.forEach(book -> {

            long currentQuantity = book.getQuantity();
            long totalBooks = getTotalBooks(isbnList, book);

            long totalQuantity = currentQuantity - totalBooks;

            if (totalQuantity < 0l) {
                throw new PurchaseException("Not enough quantity for book with ISBN " + book.getIsbn(),
                        BookStoreErrorCodes.NOT_ENOUGH_BOOKS.getErrorCode());

            }

            if (totalQuantity == 0l) {
                book.setSold(true);
            }

            book.setQuantity(totalQuantity);

        });

        booksRepository.saveAll(books);
    }

    private long getTotalBooks(List<String> purchasedBooks, List<String> freeBooks, BookEntity book) {
        long totalFreeBooks = freeBooks.stream().filter(isbn -> isbn.equals(book.getIsbn())).count();
        long totalPurchasedBooks = getTotalBooks(purchasedBooks, book);
        return totalPurchasedBooks - totalFreeBooks;
    }

    private long getTotalBooks(List<String> purchasedBooks, BookEntity book) {
        return purchasedBooks.stream().filter(isbn -> isbn.equals(book.getIsbn())).count();
    }

    private void updateClientLoyaltyPoints(ClientEntity client, Long loyaltyPoints, Long usedPoints) {
        client.setLoyaltyPoints(client.getLoyaltyPoints() + loyaltyPoints - usedPoints);

        clientsRepository.save(client);
    }

    private void checkLoyaltyPoints(List<String> isbnFreeList, ClientEntity client) {

        int loyaltyPointsNeeded = isbnFreeList == null ? 0 : isbnFreeList.size() * 10;
        if (loyaltyPointsNeeded != 0 && client.getLoyaltyPoints() < loyaltyPointsNeeded) {
            throw new PurchaseException("Not enough loyalty points to receive the free books",
                    BookStoreErrorCodes.NOT_ENOUGH_LOYALTY_POINTS.getErrorCode());
        }
    }

    private List<BookEntity> getBooks(OrderEntity order) {
        if (order.getPurchasedBook() == null || order.getPurchasedBook().isEmpty()) {
            throw new PurchaseException("The list of books cannot be null or empty",
                    BookStoreErrorCodes.INCORRECT_ORDER.getErrorCode());
        }

        // Check if the books in the order exist in the list of available books
        // If not, throw an exception
        List<BookEntity> books = booksRepository.findAllByIsbn(order.getPurchasedBook());
        if (books == null || books.isEmpty()) {
            throw new PurchaseException("No books found for the given ISBN list",
                    BookStoreErrorCodes.BOOK_NOT_FOUND.getErrorCode());
        }

        List<String> dbIsbnList = books.stream()
                .map(BookEntity::getIsbn)
                .collect(Collectors.toList());

        List<String> nonExistingISBNList = order.getPurchasedBook().stream()
                .filter(isbn -> !dbIsbnList.contains(isbn))
                .collect(Collectors.toList());
        if (nonExistingISBNList.size() > 0) {
            throw new PurchaseException("Book with ISBN " + nonExistingISBNList + " does not exist",
                    BookStoreErrorCodes.BOOK_NOT_FOUND.getErrorCode());
        }

        // Check if the free books in the order exist in the list of available books
        // If not, throw an exception
        if (order.getFreeBooks() != null) {
            List<String> nonExistIsbnFreeList = order.getFreeBooks()
                    .stream()
                    .filter(isbn -> !order.getPurchasedBook().contains(isbn))
                    .collect(Collectors.toList());
            if (nonExistIsbnFreeList.size() > 0) {
                throw new PurchaseException(
                        "To use the loyalty the point the books with ISBN " + nonExistIsbnFreeList
                                + " should be in the list of purchased books.",
                        BookStoreErrorCodes.BOOK_NOT_FOUND.getErrorCode());
            }
        }

        return books;
    }

    private ClientEntity getClient(OrderEntity order) {
        if (order.getClientId() == null) {
            throw new PurchaseException("Client ID cannot be null",
                    BookStoreErrorCodes.CLIENT_NOT_FOUND.getErrorCode());
        }

        ClientEntity client = clientsRepository.findById(order.getClientId())
                .orElseThrow(() -> new PurchaseException("Client with ID " + order.getClientId() + " not found",
                        BookStoreErrorCodes.CLIENT_NOT_FOUND.getErrorCode()));
        return client;
    }

}
