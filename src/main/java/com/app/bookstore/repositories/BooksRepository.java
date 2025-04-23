package com.app.bookstore.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.bookstore.entities.BookEntity;

@Repository
public interface BooksRepository extends JpaRepository<BookEntity, Long> {

    @Query("SELECT b FROM BookEntity b WHERE b.isbn IN :isbnList AND b.sold = false")
	public List<BookEntity> findAllByIsbn(@Param("isbnList") List<String> isbnList);

    @Query("SELECT b FROM BookEntity b WHERE b.sold = false")
    public List<BookEntity> findAllAvailableBooks();
}
