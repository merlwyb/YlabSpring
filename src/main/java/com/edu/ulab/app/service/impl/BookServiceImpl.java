package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book bookToCreate = bookMapper.bookDtoToBook(bookDto);
        return bookMapper.bookToBookDto(bookRepository.create(bookToCreate));
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book bookToUpdate = bookMapper.bookDtoToBook(bookDto);
        return bookMapper.bookToBookDto(bookRepository.update(bookToUpdate));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookMapper.bookToBookDto(bookRepository.getById(id));
    }

    @Override
    public List<BookDto> getAllBooksByUserId(Long id) {
        return bookRepository.getAllByUserId(id)
                .stream()
                .map(bookMapper::bookToBookDto)
                .collect(Collectors.toList());
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
