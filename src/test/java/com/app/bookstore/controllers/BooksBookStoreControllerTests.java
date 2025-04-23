
package com.app.bookstore.controllers;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.BDDMockito.given;

import com.app.bookstore.dtos.BookDto;
import com.app.bookstore.dtos.BooksDto;
import com.app.bookstore.entities.BookEntity;
import com.app.bookstore.repositories.BooksRepository;
import com.app.bookstore.repositories.ClientsRepository;
import com.app.bookstore.types.BookType;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksBookStoreControllerTests {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @MockitoBean
        private ClientsRepository clientsRepository;

        @MockitoBean
        private BooksRepository booksRepository;

        @Test
        void testGetBooksEndpoint() {

                given(booksRepository.findAllAvailableBooks())
                                .willReturn(getBooks());

                ResponseEntity<BooksDto> response = restTemplate.exchange(
                                "http://localhost:" + port + "/bookstore/books/",
                                HttpMethod.GET,
                                null,
                                BooksDto.class);

                assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody())
                                .usingRecursiveComparison()
                                .isEqualTo(getBooksDto());
        }

        private BooksDto getBooksDto() {
                final List<BookDto> books = new ArrayList<>();

                books.add(new BookDto("The Midnight Library", BookType.NEW_RELEASE.name(), 10L, "Matt Haig",
                                "Canongate Books",
                                "9781786892737",
                                "A novel about a library that allows you to explore alternate lives you could have lived.",
                                2005));
                books.add(new BookDto(
                                "Where the Crawdads Sing",
                                BookType.OLD_EDITIONS.name(),
                                10L,
                                "Delia Owens",
                                "G.P. Putnam's Sons", "9780735219090",
                                "A coming-of-age story set in the marshes of North Carolina.", 2012));
                books.add(new BookDto("Pride and Prejudice", BookType.REGULAR.name(), 10L, "Jane Austen", "T. Egerton",
                                "9780141439518",
                                "A romantic novel that critiques the British landed gentry at the end of the 18th century.",
                                1990));
                return new BooksDto(books);
        }

        private List<BookEntity> getBooks() {
                final List<BookEntity> books = new ArrayList<>();

                books.add(BookEntity.builder()
                                .type(BookType.NEW_RELEASE)
                                .title("The Midnight Library")
                                .price(10L)
                                .author("Matt Haig")
                                .publisher("Canongate Books")
                                .isbn("9781786892737")
                                .description("A novel about a library that allows you to explore alternate lives you could have lived.")
                                .publicationYear(2005)
                                .build());
                books.add(BookEntity.builder()
                                .type(BookType.OLD_EDITIONS)
                                .title("Where the Crawdads Sing")
                                .price(10L)
                                .author("Delia Owens")
                                .publisher("G.P. Putnam's Sons")
                                .isbn("9780735219090")
                                .description("A coming-of-age story set in the marshes of North Carolina.")
                                .publicationYear(2012)
                                .build());
                books.add(BookEntity.builder()
                                .type(BookType.REGULAR)
                                .title("Pride and Prejudice")
                                .price(10L)
                                .author("Jane Austen")
                                .publisher("T. Egerton")
                                .isbn("9780141439518")
                                .description("A romantic novel that critiques the British landed gentry at the end of the 18th century.")
                                .publicationYear(1990)
                                .build());
                return books;
        }
}