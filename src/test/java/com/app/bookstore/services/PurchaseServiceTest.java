package com.app.bookstore.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import com.app.bookstore.entities.BookEntity;
import com.app.bookstore.entities.ClientEntity;
import com.app.bookstore.entities.OrderEntity;
import com.app.bookstore.entities.PurchaseEntity;
import com.app.bookstore.exceptions.PurchaseException;
import com.app.bookstore.repositories.BooksRepository;
import com.app.bookstore.repositories.ClientsRepository;
import com.app.bookstore.types.BookType;

@SpringBootTest
public class PurchaseServiceTest {

    @Test
    @Description("Sanity test")
    public void sanity() {
        assertThat(new PurchaseService(null, null)).isNotNull();
    }

    @Test
    @Description("Test purchase service to calculate and order with discounts and loyalty points")
    public void testPurchaseServiceWithNullOrder() {
        ClientsRepository clientsRepository = Mockito.mock(ClientsRepository.class);
        BooksRepository booksRepository = Mockito.mock(BooksRepository.class);
        PurchaseService service = new PurchaseService(clientsRepository, booksRepository);

        when(clientsRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(ClientEntity.builder()
                        .id(1L)
                        .name("John Doe")
                        .loyaltyPoints(100L)
                        .build()));

        when(booksRepository.findAllByIsbn(Mockito.anyList())).thenReturn(getRepositoryBooks());

        OrderEntity order = OrderEntity.builder()
                .clientId(1L)
                .purchasedBook(List.of(
                        "978-1-23456-789-0",
                        "978-1-23456-789-1",
                        "978-1-23456-789-2",
                        "978-1-23456-789-3",
                        "978-1-23456-789-4",
                        "978-1-23456-789-5",
                        "978-1-23456-789-6",
                        "978-1-23456-789-7",
                        "978-1-23456-789-8",
                        "978-1-23456-789-9"))
                .freeBooks(List.of(
                        "978-1-23456-789-0", // This book will be not included in the calculation as it is a free book becase it is a new release
                        "978-1-23456-789-2",
                        "978-1-23456-789-4",
                        "978-1-23456-789-5",
                        "978-1-23456-789-7",
                        "978-1-23456-789-8"))
                .build();

        assertThat(service.run(order)).usingRecursiveComparison()
                .ignoringFields("purchaseDate", "books.sold")
                .isEqualTo(
                        PurchaseEntity.builder()
                                .client(ClientEntity.builder()
                                        .id(1L)
                                        .name("John Doe")
                                        .loyaltyPoints(55L)
                                        .build())
                                .books(getExistingBooks())
                                .loyaltyPoints(5L)
                                .totalPrice(2650L)
                                .purchaseDate(null)
                                .build());
    }

    @Test
    @Description("Perchuse service with order with not enough books")
    public void testPurchaseOrderNotEnoughBooks () {
        ClientsRepository clientsRepository = Mockito.mock(ClientsRepository.class);
        BooksRepository booksRepository = Mockito.mock(BooksRepository.class);
        PurchaseService service = new PurchaseService(clientsRepository, booksRepository);

        when(clientsRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(ClientEntity.builder()
                        .id(1L)
                        .name("John Doe")
                        .loyaltyPoints(100L)
                        .build()));

        when(booksRepository.findAllByIsbn(Mockito.anyList())).thenReturn(getSmallRepository());

        OrderEntity order = OrderEntity.builder()
                .clientId(1L)
                .purchasedBook(List.of(
                        "978-1-23456-789-2",
                        "978-1-23456-789-2",
                        "978-1-23456-789-2",
                        "978-1-23456-789-4"))
                .freeBooks(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> service.run(order))
            .isInstanceOf(PurchaseException.class)
            .hasMessage("Not enough quantity for book with ISBN 978-1-23456-789-2");
    }

    @Test
    @Description("Perchuse service with order with less than 3 books")
    public void testPurchaseOrderWithLess3Book () {
        ClientsRepository clientsRepository = Mockito.mock(ClientsRepository.class);
        BooksRepository booksRepository = Mockito.mock(BooksRepository.class);
        PurchaseService service = new PurchaseService(clientsRepository, booksRepository);

        when(clientsRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(ClientEntity.builder()
                        .id(1L)
                        .name("John Doe")
                        .loyaltyPoints(100L)
                        .build()));

        when(booksRepository.findAllByIsbn(Mockito.anyList())).thenReturn(getSmallRepository());

        OrderEntity order = OrderEntity.builder()
                .clientId(1L)
                .purchasedBook(List.of(
                        "978-1-23456-789-2",
                        "978-1-23456-789-4"))
                .freeBooks(Collections.emptyList())
                .build();

        assertThat(service.run(order)).usingRecursiveComparison()
                .ignoringFields("purchaseDate", "books.sold")
                .isEqualTo(
                        PurchaseEntity.builder()
                                .client(ClientEntity.builder()
                                        .id(1L)
                                        .name("John Doe")
                                        .loyaltyPoints(102L)
                                        .build())
                                .books(getSmallOrder())
                                .loyaltyPoints(2L)
                                .totalPrice(510L)
                                .purchaseDate(null)
                                .build());
    }

    private List<BookEntity> getSmallOrder() {
        return List.of(

                BookEntity.builder()
                        .id(3l)
                        .title("Old Edition Book")
                        .quantity(1l)
                        .type(BookType.OLD_EDITIONS)
                        .price(200L)
                        .isbn("978-1-23456-789-2")
                        .author("Author C")
                        .publisher("Publisher C")
                        .description("")
                        .publicationYear(2010)
                        .build(),
                BookEntity.builder()
                        .id(5l)
                        .title("Another Regular Book")
                        .quantity(0l)
                        .type(BookType.REGULAR)
                        .price(350L)
                        .isbn("978-1-23456-789-4")
                        .author("Author E")
                        .publisher("Publisher E")
                        .description("")
                        .publicationYear(2019)
                        .build());
    }

    private List<BookEntity> getSmallRepository() {
        return List.of(

                BookEntity.builder()
                        .id(3l)
                        .title("Old Edition Book")
                        .quantity(2l)
                        .type(BookType.OLD_EDITIONS)
                        .price(200L)
                        .isbn("978-1-23456-789-2")
                        .author("Author C")
                        .publisher("Publisher C")
                        .description("")
                        .publicationYear(2010)
                        .build(),
                BookEntity.builder()
                        .id(5l)
                        .title("Another Regular Book")
                        .quantity(1l)
                        .type(BookType.REGULAR)
                        .price(350L)
                        .isbn("978-1-23456-789-4")
                        .author("Author E")
                        .publisher("Publisher E")
                        .description("")
                        .publicationYear(2019)
                        .build());
    }

    private List<BookEntity> getExistingBooks() {
        return List.of(
                BookEntity.builder()
                        .id(1l)
                        .title("New Release Book")
                        .quantity(1l)
                        .type(BookType.NEW_RELEASE)
                        .price(500L)
                        .isbn("978-1-23456-789-0")
                        .author("Author A")
                        .publisher("Publisher A")
                        .description("")
                        .publicationYear(2023)
                        .build(),
                BookEntity.builder()
                        .id(2l)
                        .title("Regular Book")
                        .quantity(0l)
                        .type(BookType.NEW_RELEASE)
                        .price(300L)
                        .isbn("978-1-23456-789-1")
                        .author("Author B")
                        .publisher("Publisher B")
                        .description("")
                        .publicationYear(2020)
                        .build(),
                BookEntity.builder()
                        .id(3l)
                        .title("Old Edition Book")
                        .quantity(1l)
                        .type(BookType.OLD_EDITIONS)
                        .price(200L)
                        .isbn("978-1-23456-789-2")
                        .author("Author C")
                        .publisher("Publisher C")
                        .description("")
                        .publicationYear(2010)
                        .build(),
                BookEntity.builder()
                        .id(4l)
                        .title("Another New Release")
                        .quantity(0l)
                        .type(BookType.NEW_RELEASE)
                        .price(600L)
                        .isbn("978-1-23456-789-3")
                        .author("Author D")
                        .publisher("Publisher D")
                        .description("")
                        .publicationYear(2023)
                        .build(),
                BookEntity.builder()
                        .id(5l)
                        .title("Another Regular Book")
                        .quantity(1l)
                        .type(BookType.REGULAR)
                        .price(350L)
                        .isbn("978-1-23456-789-4")
                        .author("Author E")
                        .publisher("Publisher E")
                        .description("")
                        .publicationYear(2019)
                        .build(),
                BookEntity.builder()
                        .id(6l)
                        .title("Another Old Edition")
                        .quantity(0l)
                        .type(BookType.OLD_EDITIONS)
                        .price(250L)
                        .isbn("978-1-23456-789-5")
                        .author("Author F")
                        .publisher("Publisher F")
                        .description("")
                        .publicationYear(2005)
                        .build(),
                BookEntity.builder()
                        .id(7l)
                        .title("Yet Another New Release")
                        .quantity(1l)
                        .type(BookType.NEW_RELEASE)
                        .price(550L)
                        .isbn("978-1-23456-789-6")
                        .author("Author G")
                        .publisher("Publisher G")
                        .description("")
                        .publicationYear(2023)
                        .build(),
                BookEntity.builder()
                        .id(8l)
                        .title("Yet Another Regular Book")
                        .quantity(0l)
                        .type(BookType.REGULAR)
                        .price(400L)
                        .isbn("978-1-23456-789-7")
                        .author("Author H")
                        .publisher("Publisher H")
                        .description("")
                        .publicationYear(2018)
                        .build(),
                BookEntity.builder()
                        .id(9l)
                        .title("Yet Another Old Edition")
                        .quantity(1l)
                        .type(BookType.OLD_EDITIONS)
                        .price(300L)
                        .isbn("978-1-23456-789-8")
                        .author("Author I")
                        .publisher("Publisher I")
                        .description("")
                        .publicationYear(2000)
                        .build(),
                BookEntity.builder()
                        .id(10l)
                        .title("Final New Release")
                        .quantity(0l)
                        .type(BookType.NEW_RELEASE)
                        .price(700L)
                        .isbn("978-1-23456-789-9")
                        .author("Author J")
                        .publisher("Publisher J")
                        .description("")
                        .publicationYear(2023)
                        .build());
    }

    private List<BookEntity> getRepositoryBooks() {
        return List.of(
                BookEntity.builder()
                        .id(1l)
                        .title("New Release Book")
                        .quantity(2l)
                        .type(BookType.NEW_RELEASE)
                        .price(500L)
                        .isbn("978-1-23456-789-0")
                        .author("Author A")
                        .publisher("Publisher A")
                        .description("")
                        .publicationYear(2023)
                        .build(),
                BookEntity.builder()
                        .id(2l)
                        .title("Regular Book")
                        .quantity(1l)
                        .type(BookType.NEW_RELEASE)
                        .price(300L)
                        .isbn("978-1-23456-789-1")
                        .author("Author B")
                        .publisher("Publisher B")
                        .description("")
                        .publicationYear(2020)
                        .build(),
                BookEntity.builder()
                        .id(3l)
                        .title("Old Edition Book")
                        .quantity(2l)
                        .type(BookType.OLD_EDITIONS)
                        .price(200L)
                        .isbn("978-1-23456-789-2")
                        .author("Author C")
                        .publisher("Publisher C")
                        .description("")
                        .publicationYear(2010)
                        .build(),
                BookEntity.builder()
                        .id(4l)
                        .title("Another New Release")
                        .quantity(1l)
                        .type(BookType.NEW_RELEASE)
                        .price(600L)
                        .isbn("978-1-23456-789-3")
                        .author("Author D")
                        .publisher("Publisher D")
                        .description("")
                        .publicationYear(2023)
                        .build(),
                BookEntity.builder()
                        .id(5l)
                        .title("Another Regular Book")
                        .quantity(2l)
                        .type(BookType.REGULAR)
                        .price(350L)
                        .isbn("978-1-23456-789-4")
                        .author("Author E")
                        .publisher("Publisher E")
                        .description("")
                        .publicationYear(2019)
                        .build(),
                BookEntity.builder()
                        .id(6l)
                        .title("Another Old Edition")
                        .quantity(1l)
                        .type(BookType.OLD_EDITIONS)
                        .price(250L)
                        .isbn("978-1-23456-789-5")
                        .author("Author F")
                        .publisher("Publisher F")
                        .description("")
                        .publicationYear(2005)
                        .build(),
                BookEntity.builder()
                        .id(7l)
                        .title("Yet Another New Release")
                        .quantity(2l)
                        .type(BookType.NEW_RELEASE)
                        .price(550L)
                        .isbn("978-1-23456-789-6")
                        .author("Author G")
                        .publisher("Publisher G")
                        .description("")
                        .publicationYear(2023)
                        .build(),
                BookEntity.builder()
                        .id(8l)
                        .title("Yet Another Regular Book")
                        .quantity(1l)
                        .type(BookType.REGULAR)
                        .price(400L)
                        .isbn("978-1-23456-789-7")
                        .author("Author H")
                        .publisher("Publisher H")
                        .description("")
                        .publicationYear(2018)
                        .build(),
                BookEntity.builder()
                        .id(9l)
                        .title("Yet Another Old Edition")
                        .quantity(2l)
                        .type(BookType.OLD_EDITIONS)
                        .price(300L)
                        .isbn("978-1-23456-789-8")
                        .author("Author I")
                        .publisher("Publisher I")
                        .description("")
                        .publicationYear(2000)
                        .build(),
                BookEntity.builder()
                        .id(10l)
                        .title("Final New Release")
                        .quantity(1l)
                        .type(BookType.NEW_RELEASE)
                        .price(700L)
                        .isbn("978-1-23456-789-9")
                        .author("Author J")
                        .publisher("Publisher J")
                        .description("")
                        .publicationYear(2023)
                        .build());
    }
}