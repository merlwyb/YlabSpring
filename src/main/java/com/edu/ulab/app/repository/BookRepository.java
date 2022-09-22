package com.edu.ulab.app.repository;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NoSuchEntityException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BookRepository {

    private long id = 1;
    private final Map<Long, Book> bookMap = new HashMap<>();

    public Book create(Book book) {
        if (checkOnNullAndEmptyValues(book))
            return new Book();
        book.setId(id);
        bookMap.put(id++, book);
        return book;
    }

    public Book update(Book book) {
        if (book.getId() != null) {
            if (checkOnNullAndEmptyValues(book))
                return new Book();
            bookMap.put(book.getId(), book);
        } else {
            return create(book);
        }
        return book;
    }

    public List<Book> getAllByUserId(Long userId) {
        return bookMap.entrySet()
                .stream()
                .filter(a -> a.getValue().getUserId().equals(userId))
                .map(Map.Entry::getKey)
                .map(this::getById)
                .toList();
    }

    public Book getById(Long id) {
        Book book = bookMap.get(id);
        if (book == null) {
            return new Book();
        }
        return book;
    }

    public void deleteAllByUserId(Long userId) {
        bookMap.entrySet()
                .stream()
                .filter(a -> a.getValue().getUserId().equals(userId))
                .map(Map.Entry::getKey)
                .toList()
                .forEach(this::deleteById);
    }

    public void deleteById(Long id) {
        bookMap.remove(id);
    }

    private boolean checkOnNullAndEmptyValues(Book book) {
        return book.getTitle() == null ||
                book.getTitle().matches("^\s*$") ||
                book.getAuthor() == null ||
                book.getAuthor().matches("^\s*$") ||
                book.getPageCount() <= 0;
    }
}
