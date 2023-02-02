package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.BookService;
import com.github.dockerjava.api.exception.NotAcceptableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           UserRepository userRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Person gotPerson = userRepository.findById(bookDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User with such id not found"));

        Book book = bookMapper.bookDtoToBook(bookDto);
        book.setPerson(gotPerson);
        log.info("Mapped book: {}", book);
        checkOnNullAndEmptyValues(book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);

        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Person gotPerson = userRepository.findById(bookDto.getUserId())
                .orElseThrow(() -> new NotFoundException("User with such id not found"));

        Book book = bookMapper.bookDtoToBook(bookDto);
        book.setPerson(gotPerson);
        log.info("Mapped book: {}", book);
        checkOnNullAndEmptyValues(book);
        Book updatedBook = bookRepository.save(book);
        log.info("Updated book: {}", updatedBook);
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book gotBook = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book was not found"));
        log.info("Got book: {}", gotBook);
        return bookMapper.bookToBookDto(gotBook);
    }

    @Override
    public List<BookDto> getAllBooksByUserId(Long userId) {
        log.info("Trying to get all book with userId: {}", userId);
        return bookRepository.findAllByPersonId(userId)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public void deleteBookById(Long id) {
        if (!bookRepository.existsById(id))
            throw new NotFoundException("No book with requested id to delete");
        log.info("Trying to delete book with id: {}", id);
        bookRepository.deleteById(id);
        log.info("Book was deleted with id: {}", id);
    }

    @Override
    public void deleteAllBooksByUserId(Long userId) {
        log.info("Trying to delete books with userId: {}", userId);
        bookRepository.deleteAllByPersonId(userId);
        log.info("Books was deleted with userId: {}", userId);
    }

    private void checkOnNullAndEmptyValues(Book book) {
        if (book.getTitle() == null ||
                book.getTitle().matches("^\s*$") ||
                book.getAuthor() == null ||
                book.getAuthor().matches("^\s*$") ||
                book.getPageCount() <= 0)
            throw new EmptyFieldException("Book fields cannot be equals null or empty");
    }
}
