package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto userDto);

    BookDto updateBook(BookDto userDto);

    BookDto getBookById(Long id);
    List<BookDto> getAllBooksByUserId(Long id);

    void deleteBookById(Long id);
    void deleteAllBooksByUserId(Long id);
}
