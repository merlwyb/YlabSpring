package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        return bookRepository.create(bookDto);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        return bookRepository.update(bookDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookRepository.getById(id);
    }

    @Override
    public List<BookDto> getAllBooksByUserId(Long id) {
        return bookRepository.getAllByUserId(id);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public void deleteAllBooksByUserId(Long id) {
        bookRepository.deleteAllByUserId(id);
    }
}
