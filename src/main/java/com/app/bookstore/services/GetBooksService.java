package com.app.bookstore.services;

import org.springframework.stereotype.Service;

import com.app.bookstore.entities.BooksEntity;
import com.app.bookstore.entities.EmptyEnitity;
import com.app.bookstore.repositories.BooksRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetBooksService implements StoreService<EmptyEnitity> {
    
    private BooksRepository booksRepository;

    // This method will be used to retrieve a list of books from the database.
    @Override
    public BooksEntity run(EmptyEnitity entity) {
        return new BooksEntity(booksRepository.findAllAvailableBooks());
    }

}
