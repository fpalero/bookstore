package com.app.bookstore.controllers;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.app.bookstore.dtos.BookDto;
import com.app.bookstore.dtos.ClientDto;
import com.app.bookstore.dtos.OrderDto;
import com.app.bookstore.dtos.PurchaseDto;
import com.app.bookstore.entities.BookEntity;
import com.app.bookstore.entities.ClientEntity;
import com.app.bookstore.exceptions.BookStoreError;
import com.app.bookstore.repositories.BooksRepository;
import com.app.bookstore.repositories.ClientsRepository;
import com.app.bookstore.types.BookType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClientsBookStoreControllerTests {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @MockitoBean
        private ClientsRepository clientsRepository;

        @MockitoBean
        private BooksRepository booksRepository;

        @Test
        @Description("Test to get client loyalty points endpoint")
        void testGetLoyaltyPoints() {

                given(clientsRepository.findById(1L))
                                .willReturn(java.util.Optional.of(ClientEntity.builder()
                                                .id(1l)
                                                .name("John Doe")
                                                .email("email")
                                                .phone("1234567890")
                                                .address("123 Main St")
                                                .loyaltyPoints(100L)
                                                .build()));

                ResponseEntity<Long> response = this.restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1/loyalty",
                                HttpMethod.GET,
                                null,
                                Long.class);

                assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody()).isEqualTo(100L);
        }

        @Test
        @Description("Test loyalty endpoint when client doesn't exist")
        void testGetLoyaltyPointsWhenClientDoesNotExist() {
                given(clientsRepository.findById(1000L))
                                .willReturn(java.util.Optional.empty());

                ResponseEntity<BookStoreError> response = this.restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1000/loyalty",
                                HttpMethod.GET,
                                null,
                                BookStoreError.class);

                assertThat(response.getStatusCode().is4xxClientError()).isTrue();
                assertThat(response.getBody())
                                .isEqualTo(new BookStoreError(100,
                                                "Book Store exception thrown: Client with ID 1000 not found"));
        }

        @Test
        @Description("Test to purchase books endpoint")
        void testPurchaseBooks() {
                List<String> isbnList = Arrays.asList("978-1-23456-789-0", "978-1-23456-789-1", "978-1-23456-789-2",
                                "978-1-23456-789-3", "978-1-23456-789-4", "978-1-23456-789-5", "978-1-23456-789-6",
                                "978-1-23456-789-7", "978-1-23456-789-8", "978-1-23456-789-9");

                given(clientsRepository.findById(1L))
                                .willReturn(java.util.Optional.of(ClientEntity.builder()
                                                .id(1l)
                                                .name("Mocked Client Name")
                                                .email("mocked.email@example.com")
                                                .phone("1234455678")
                                                .address("street 123")
                                                .loyaltyPoints(100L)
                                                .build()));

                given(booksRepository.findAllByIsbn(isbnList))
                                .willReturn(getExistingBooks());

                HttpEntity<OrderDto> entity = new HttpEntity<>(new OrderDto(isbnList, Collections.emptyList()));
                ResponseEntity<PurchaseDto> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1/purchase",
                                HttpMethod.POST,
                                entity,
                                PurchaseDto.class);

                assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody())
                                .usingRecursiveComparison()
                                .ignoringFields("purchaseDate")
                                .isEqualTo(getExpetedPurchase());
        }

        @Test
        @Description("Test a purchase where the client try to use the loyalty points on a NEW RELEASE book type. Expected result, the purchase don't use the loyalty points")
        void testPurchaseBooksLoyaltyPointsCannotBeUsedOnNewRelease() {
                List<String> isbnList = Arrays.asList("978-1-23456-789-0", "978-1-23456-789-1", "978-1-23456-789-2",
                                "978-1-23456-789-3", "978-1-23456-789-4", "978-1-23456-789-5", "978-1-23456-789-6",
                                "978-1-23456-789-7", "978-1-23456-789-8", "978-1-23456-789-9");

                given(clientsRepository.findById(1L))
                                .willReturn(java.util.Optional.of(ClientEntity.builder()
                                                .id(1l)
                                                .name("Mocked Client Name")
                                                .email("mocked.email@example.com")
                                                .phone("1234455678")
                                                .address("street 123")
                                                .loyaltyPoints(100L)
                                                .build()));

                given(booksRepository.findAllByIsbn(isbnList))
                                .willReturn(getExistingBooks());

                HttpEntity<OrderDto> entity = new HttpEntity<>(new OrderDto(isbnList, Arrays.asList("978-1-23456-789-0")));
                ResponseEntity<PurchaseDto> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1/purchase",
                                HttpMethod.POST,
                                entity,
                                PurchaseDto.class);

                assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody())
                                .usingRecursiveComparison()
                                .ignoringFields("purchaseDate")
                                .isEqualTo(getExpetedPurchase());
        }

        private List<BookEntity> getSomeBooks() {
                return List.of(
                                BookEntity.builder()
                                                .id(2l)
                                                .title("Regular Book")
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
                                                .type(BookType.NEW_RELEASE)
                                                .price(600L)
                                                .isbn("978-1-23456-789-3")
                                                .author("Author D")
                                                .publisher("Publisher D")
                                                .description("")
                                                .publicationYear(2023)
                                                .build(),
                                BookEntity.builder()
                                                .id(6l)
                                                .title("Another Old Edition")
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
                                                .type(BookType.NEW_RELEASE)
                                                .price(700L)
                                                .isbn("978-1-23456-789-9")
                                                .author("Author J")
                                                .publisher("Publisher J")
                                                .description("")
                                                .publicationYear(2023)
                                                .build());
        }

        private List<BookEntity> getExistingBooks() {
                return List.of(
                                BookEntity.builder()
                                                .id(1l)
                                                .title("New Release Book")
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
                                                .type(BookType.NEW_RELEASE)
                                                .price(700L)
                                                .isbn("978-1-23456-789-9")
                                                .author("Author J")
                                                .publisher("Publisher J")
                                                .description("")
                                                .publicationYear(2023)
                                                .build());
        }

        private List<BookDto> getDtoBooks() {
                return List.of(
                                new BookDto("New Release Book", "NEW_RELEASE", 500L, "Author A", "Publisher A",
                                                "978-1-23456-789-0", "",
                                                2023),
                                new BookDto("Regular Book", "NEW_RELEASE", 300L, "Author B", "Publisher B",
                                                "978-1-23456-789-1", "",
                                                2020),
                                new BookDto("Old Edition Book", "OLD_EDITIONS", 200L, "Author C", "Publisher C",
                                                "978-1-23456-789-2",
                                                "", 2010),
                                new BookDto("Another New Release", "NEW_RELEASE", 600L, "Author D", "Publisher D",
                                                "978-1-23456-789-3",
                                                "", 2023),
                                new BookDto("Another Regular Book", "REGULAR", 350L, "Author E", "Publisher E",
                                                "978-1-23456-789-4", "",
                                                2019),
                                new BookDto("Another Old Edition", "OLD_EDITIONS", 250L, "Author F", "Publisher F",
                                                "978-1-23456-789-5",
                                                "", 2005),
                                new BookDto("Yet Another New Release", "NEW_RELEASE", 550L, "Author G",
                                                "Publisher G", "978-1-23456-789-6", "", 2023),
                                new BookDto("Yet Another Regular Book", "REGULAR", 400L, "Author H", "Publisher H",
                                                "978-1-23456-789-7",
                                                "", 2018),
                                new BookDto("Yet Another Old Edition", "OLD_EDITIONS", 300L, "Author I",
                                                "Publisher I", "978-1-23456-789-8", "", 2000),
                                new BookDto("Final New Release", "NEW_RELEASE", 700L, "Author J", "Publisher J",
                                                "978-1-23456-789-9",
                                                "", 2023));
        }

        private PurchaseDto getExpetedPurchase() {
                ClientDto clientDto = new ClientDto("Mocked Client Name", "mocked.email@example.com", "1234455678",
                                "street 123", 110L);
                return new PurchaseDto(clientDto, getDtoBooks(), 10L, 3887L, "2025-04-21T21:54:29.070384");
        }

        private PurchaseDto getExpetedPurchaseUsingLoyaltyPoints() {
                ClientDto clientDto = new ClientDto("Mocked Client Name", "mocked.email@example.com", "1234455678",
                                "street 123", 99L);
                return new PurchaseDto(clientDto, getDtoBooks(), 9L, 3572L, "2025-04-21T21:54:29.070384");
        }

        @Test
        @Description("Test to purchase free books with not enough loyalty points")
        public void testPurchaseBooksWithNotEnoughLoyaltyPoints() {
                List<String> isbnList = Arrays.asList("978-1-23456-789-0", "978-1-23456-789-1", "978-1-23456-789-2",
                                "978-1-23456-789-3", "978-1-23456-789-4", "978-1-23456-789-5", "978-1-23456-789-6",
                                "978-1-23456-789-7", "978-1-23456-789-8", "978-1-23456-789-9");

                given(clientsRepository.findById(1L))
                                .willReturn(java.util.Optional.of(ClientEntity.builder()
                                                .id(1l)
                                                .name("Mocked Client Name")
                                                .email("mocked.email@example.com")
                                                .phone("1234455678")
                                                .address("street 123")
                                                .loyaltyPoints(5L)
                                                .build()));

                given(booksRepository.findAllByIsbn(isbnList))
                                .willReturn(getExistingBooks());

                HttpEntity<OrderDto> entity = new HttpEntity<>(
                                new OrderDto(isbnList, Arrays.asList("978-1-23456-789-4")));

                ResponseEntity<BookStoreError> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1/purchase",
                                HttpMethod.POST,
                                entity,
                                BookStoreError.class);

                assertThat(response.getStatusCode().is4xxClientError()).isTrue();
                assertThat(response.getBody()).isEqualTo(new BookStoreError(103,
                                "Book Store exception thrown: Not enough loyalty points to receive the free books"));
        }

        @Test
        @Description("Test to purchase free books with enough loyalty points")
        public void testPurchaseBooksWithEnoughLoyaltyPoints() {
                List<String> isbnList = Arrays.asList("978-1-23456-789-0", "978-1-23456-789-1", "978-1-23456-789-2",
                                "978-1-23456-789-3", "978-1-23456-789-4", "978-1-23456-789-5", "978-1-23456-789-6",
                                "978-1-23456-789-7", "978-1-23456-789-8", "978-1-23456-789-9");

                given(clientsRepository.findById(1L))
                                .willReturn(java.util.Optional.of(ClientEntity.builder()
                                                .id(1l)
                                                .name("Mocked Client Name")
                                                .email("mocked.email@example.com")
                                                .phone("1234455678")
                                                .address("street 123")
                                                .loyaltyPoints(100L)
                                                .build()));

                given(booksRepository.findAllByIsbn(isbnList))
                                .willReturn(getExistingBooks());

                HttpEntity<OrderDto> entity = new HttpEntity<>(new OrderDto(
                                isbnList,
                                Arrays.asList("978-1-23456-789-4")));

                ResponseEntity<PurchaseDto> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1/purchase",
                                HttpMethod.POST,
                                entity,
                                PurchaseDto.class);

                assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody())
                                .usingRecursiveComparison()
                                .ignoringFields("purchaseDate")
                                .isEqualTo(getExpetedPurchaseUsingLoyaltyPoints());
        }

        @Test
        @Description("Test to purchase books some of them doesn't exist")
        public void testPurchaseBooksWithSomeBooksNotExist() {
                List<String> isbnList = Arrays.asList("978-1-23456-789-0", "978-1-23456-789-1", "978-1-23456-789-2",
                                "978-1-23456-789-3", "978-1-23456-789-4", "978-1-23456-789-5", "978-1-23456-789-6",
                                "978-1-23456-789-7", "978-1-23456-789-8", "978-1-23456-789-9");

                given(clientsRepository.findById(1L))
                                .willReturn(java.util.Optional.of(ClientEntity.builder()
                                                .id(1l)
                                                .name("Mocked Client Name")
                                                .email("mocked.email@example.com")
                                                .phone("1234455678")
                                                .address("street 123")
                                                .loyaltyPoints(100L)
                                                .build()));

                given(booksRepository.findAllByIsbn(isbnList))
                                .willReturn(getSomeBooks());

                HttpEntity<OrderDto> entity = new HttpEntity<>(new OrderDto(isbnList, Collections.emptyList()));
                ResponseEntity<BookStoreError> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1/purchase",
                                HttpMethod.POST,
                                entity,
                                BookStoreError.class);

                assertThat(response.getStatusCode().is4xxClientError()).isTrue();
                assertThat(response.getBody()).isEqualTo(new BookStoreError(104,
                                "Book Store exception thrown: Book with ISBN [978-1-23456-789-0, 978-1-23456-789-4] does not exist"));
        }

        @Test
        @Description("Test to purchase books with empty list")
        public void testPurchaseBooksWithEmptyList() {

                given(clientsRepository.findById(1L))
                                .willReturn(java.util.Optional.of(ClientEntity.builder()
                                                .id(1l)
                                                .name("Mocked Client Name")
                                                .email("mocked.email@example.com")
                                                .phone("1234455678")
                                                .address("street 123")
                                                .loyaltyPoints(100L)
                                                .build()));

                given(booksRepository.findAllByIsbn(Collections.emptyList()))
                                .willReturn(getSomeBooks());

                HttpEntity<OrderDto> entity = new HttpEntity<>(
                                new OrderDto(Collections.emptyList(), Collections.emptyList()));

                ResponseEntity<BookStoreError> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1/purchase",
                                HttpMethod.POST,
                                entity,
                                BookStoreError.class);

                assertThat(response.getStatusCode().is4xxClientError()).isTrue();
                assertThat(response.getBody()).isEqualTo(new BookStoreError(105,
                                "Book Store exception thrown: {isbnList=must not be empty}"));
        }

        @Test
        @Description("Test to purchase books with not existing client")
        public void testPurchaseBooksWithNotExistingClient() {
                List<String> isbnList = Arrays.asList("978-1-23456-789-0", "978-1-23456-789-1", "978-1-23456-789-2",
                                "978-1-23456-789-3", "978-1-23456-789-4", "978-1-23456-789-5", "978-1-23456-789-6",
                                "978-1-23456-789-7", "978-1-23456-789-8", "978-1-23456-789-9");
                given(clientsRepository.findById(1000L))
                                .willReturn(java.util.Optional.empty());
                HttpEntity<OrderDto> entity = new HttpEntity<>(
                                new OrderDto(isbnList, Collections.emptyList()));
                ResponseEntity<BookStoreError> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/clients/1000/purchase",
                                HttpMethod.POST,
                                entity,
                                BookStoreError.class);

                assertThat(response.getStatusCode().is4xxClientError()).isTrue();
                assertThat(response.getBody())
                                .isEqualTo(new BookStoreError(100,
                                                "Book Store exception thrown: Client with ID 1000 not found"));
        }

}
