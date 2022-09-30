package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NoSuchEntityException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
    final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, USER_ID = ? WHERE ID = ?";
    final String SELECT_SQL = "SELECT * FROM BOOK WHERE ID = ?";
    final String SELECT_ALL_SQL = "SELECT * FROM BOOK WHERE USER_ID = ?";
    final String DELETE_SQL = "DELETE FROM BOOK WHERE ID = ?";
    final String DELETE_ALL_SQL = "DELETE FROM BOOK WHERE ID = ?";

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        checkOnNullAndEmptyValues(bookDto);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        checkOnNullAndEmptyValues(bookDto);

        boolean notSucceed = jdbcTemplate.update(UPDATE_SQL, bookDto.getTitle(),
                bookDto.getAuthor(), bookDto.getPageCount(), bookDto.getUserId(), bookDto.getId()) == 0;

        if (notSucceed)
            return createBook(bookDto);

        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        Book gotBook = jdbcTemplate.query(SELECT_SQL, rs -> {
            if (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("ID"));
                book.setUserId(rs.getLong("USER_ID"));
                book.setTitle(rs.getString("TITLE"));
                book.setAuthor(rs.getString("AUTHOR"));
                book.setPageCount(rs.getLong("PAGE_COUNT"));
                return book;
            }
            return null;
        }, id);

        if (gotBook == null)
            throw new NoSuchEntityException("No book with requested id");
        return bookMapper.bookToBookDto(gotBook);
    }

    @Override
    public List<BookDto> getAllBooksByUserId(Long userId) {
        List<Book> gotBooks = jdbcTemplate.query(SELECT_ALL_SQL, new BeanPropertyRowMapper<>(Book.class), userId);
        System.out.println(gotBooks.size());
        if (gotBooks.size() == 0)
            throw new NoSuchEntityException("No books with requested userId");
        return gotBooks.stream().map(bookMapper::bookToBookDto).collect(Collectors.toList());
    }

    @Override
    public void deleteBookById(Long id) {
        if (jdbcTemplate.update(DELETE_SQL, id) == 0)
            throw new NoSuchEntityException("No book with requested id to delete");
    }

    @Override
    public void deleteAllBooksByUserId(Long userId) {
        if (jdbcTemplate.update(DELETE_ALL_SQL, userId) == 0)
            throw new NoSuchEntityException("No books with requested userId to delete");
    }

    private void checkOnNullAndEmptyValues(BookDto bookDto) {
        if (bookDto.getTitle() == null ||
                bookDto.getTitle().matches("^\s*$") ||
                bookDto.getAuthor() == null ||
                bookDto.getAuthor().matches("^\s*$") ||
                bookDto.getPageCount() <= 0)
            throw new EmptyFieldException("Book fields cannot be equals null or empty");
    }
}
