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

    public BookDto create(BookDto bookDto) {
        checkOnNullAndEmptyValues(bookDto);
        Book book = new Book(bookDto.getUserId(), bookDto.getTitle(), bookDto.getAuthor(), bookDto.getPageCount());
        bookMap.put(id, book);
        bookDto.setId(id++);

        return bookDto;
    }

    public BookDto update(BookDto bookDto) {
        if (bookDto.getId() != null) {
            checkOnNullAndEmptyValues(bookDto);
            Book book = new Book(bookDto.getUserId(), bookDto.getTitle(), bookDto.getAuthor(), bookDto.getPageCount());
            bookMap.put(bookDto.getUserId(), book);
        } else {
            return create(bookDto);
        }
        return bookDto;
    }

    public List<BookDto> getAllByUserId(Long userId) {
        return bookMap.entrySet()
                .stream()
                .filter(a -> a.getValue().getUserId().equals(userId))
                .map(Map.Entry::getKey)
                .map(this::getById)
                .toList();
    }

    public BookDto getById(Long id) {
        Book book = bookMap.get(id);
        if (book == null) {
            throw new NoSuchEntityException("Book was not found with id = " + id);
        }

        BookDto bookDto = new BookDto();
        bookDto.setId(id);
        bookDto.setUserId(book.getUserId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPageCount(book.getPageCount());

        return bookDto;
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

    private void checkOnNullAndEmptyValues(BookDto bookDto) {
        if (bookDto.getTitle() == null ||
                bookDto.getTitle().matches("^\s*$") ||
                bookDto.getAuthor() == null ||
                bookDto.getAuthor().matches("^\s*$") ||
                bookDto.getPageCount() <= 0)
            throw new EmptyFieldException("Book cannot have an empty or null values");
    }
}
