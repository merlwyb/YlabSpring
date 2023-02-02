package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги")
    void saveBook_Test() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    // update
    @Test
    @DisplayName("Обновление книги") //аналогично сохранению
    void updateBook_Test() {
        //given
        Person person = new Person();
        person.setId(2L);

        BookDto bookForUpdateDto = new BookDto();
        bookForUpdateDto.setId(1L);
        bookForUpdateDto.setUserId(2L);
        bookForUpdateDto.setTitle("Mummy");
        bookForUpdateDto.setAuthor("Aaron");
        bookForUpdateDto.setPageCount(500);

        BookDto resultOfUpdate = new BookDto();
        resultOfUpdate.setId(1L);
        resultOfUpdate.setUserId(2L);
        resultOfUpdate.setTitle("Mummy");
        resultOfUpdate.setAuthor("Aaron");
        resultOfUpdate.setPageCount(500);


        Book book = new Book();
        book.setPerson(person);
        book.setTitle("Mummy");
        book.setAuthor("Aaron");
        book.setPageCount(500);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setPerson(person);
        updatedBook.setTitle("Mummy");
        updatedBook.setAuthor("Aaron");
        updatedBook.setPageCount(500);

        //when
        when(bookMapper.bookDtoToBook(bookForUpdateDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(resultOfUpdate);


        //then
        BookDto bookDtoResult = bookService.updateBook(bookForUpdateDto);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals(2L, bookDtoResult.getUserId());
        assertEquals("Mummy", bookDtoResult.getTitle());
        assertEquals("Aaron", bookDtoResult.getAuthor());
        assertEquals(500, bookDtoResult.getPageCount());
    }

    // get
    @Test
    @DisplayName("Получение книги")
    void getBook_Test() {
        //given
        Long bookId = 1L;

        Person person = new Person();
        person.setId(1L);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book gotBook = new Book();
        gotBook.setId(1L);
        gotBook.setPageCount(1000);
        gotBook.setTitle("test title");
        gotBook.setAuthor("test author");
        gotBook.setPerson(person);

        //when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(gotBook));
        when(bookMapper.bookToBookDto(gotBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.getBookById(bookId);
        assertEquals(1L, bookDtoResult.getId());
        assertEquals(1L, bookDtoResult.getUserId());
        assertEquals("test author", bookDtoResult.getAuthor());
        assertEquals("test title", bookDtoResult.getTitle());
        assertEquals(1000, bookDtoResult.getPageCount());
    }

    // get all
    @Test
    @DisplayName("Получение всех книг по пользователю")
    void getAllBook_Test() {
        //given
        Long userId = 1L;

        Person person = new Person();
        person.setId(1L);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        BookDto result2 = new BookDto();
        result2.setId(2L);
        result2.setUserId(1L);
        result2.setAuthor("Aaron");
        result2.setTitle("Mummy");
        result2.setPageCount(500);

        Book book = new Book();
        book.setPerson(person);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPageCount(1000);

        Book book2 = new Book();
        book2.setPerson(person);
        book2.setTitle("Mummy");
        book2.setAuthor("Aaron");
        book2.setPageCount(500);


        //when
        when(bookRepository.findAllByPersonId(userId)).thenReturn(List.of(book, book2));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);
        when(bookMapper.bookToBookDto(book2)).thenReturn(result2);


        //then
        List<BookDto> bookDtoListResult = bookService.getAllBooksByUserId(userId);
        assertEquals(1L, bookDtoListResult.get(0).getId());
        assertEquals(2L, bookDtoListResult.get(1).getId());
        assertEquals(1L, bookDtoListResult.get(0).getUserId());
        assertEquals(1L, bookDtoListResult.get(1).getUserId());

    }

    // delete
    @Test
    @DisplayName("Удаление одной книги по id")
    void deleteOneBook_Test() {
        //given
        Long bookId = 1L;

        //when
        when(bookRepository.existsById(bookId)).thenReturn(true);

        //then
        bookRepository.deleteById(bookId);
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("Удаление всех книг пользователя")
    void deleteAllBook_Test() {
        //given
        Long userId = 1L;

        //when

        //then
        bookService.deleteAllBooksByUserId(userId);
        verify(bookRepository, times(1)).deleteAllByPersonId(userId);
    }

    // * failed

    @Test
    @DisplayName("Ошибка создания/обновления - неверно заполненные поля")
    void saveBook_FailOnFieldsTest() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto1 = new BookDto();
        bookDto1.setUserId(1L);
        bookDto1.setTitle("test title");
        bookDto1.setPageCount(1000);

        BookDto bookDto2 = new BookDto();
        bookDto2.setUserId(1L);
        bookDto2.setAuthor("test author");
        bookDto2.setPageCount(1000);

        BookDto bookDto3 = new BookDto();
        bookDto3.setUserId(1L);
        bookDto3.setAuthor("test author");
        bookDto3.setTitle("test title");

        Book book1 = new Book();
        book1.setPerson(person);
        book1.setTitle("test title");
        book1.setPageCount(1000);

        Book book2 = new Book();
        book2.setPerson(person);
        book2.setAuthor("test author");
        book2.setPageCount(1000);

        Book book3 = new Book();
        book3.setPerson(person);
        book3.setAuthor("test author");
        book3.setTitle("test title");



        //when
        when(bookMapper.bookDtoToBook(bookDto1)).thenReturn(book1);
        when(bookMapper.bookDtoToBook(bookDto2)).thenReturn(book2);
        when(bookMapper.bookDtoToBook(bookDto3)).thenReturn(book3);
        when(userRepository.findById(person.getId())).thenReturn(Optional.of(person));

        //then
        assertThatThrownBy(()->bookService.createBook(bookDto1))
                .isInstanceOf(EmptyFieldException.class)
                .hasMessage("Book fields cannot be equals null or empty");
        assertThatThrownBy(()->bookService.createBook(bookDto2))
                .isInstanceOf(EmptyFieldException.class)
                .hasMessage("Book fields cannot be equals null or empty");
        assertThatThrownBy(()->bookService.createBook(bookDto3))
                .isInstanceOf(EmptyFieldException.class)
                .hasMessage("Book fields cannot be equals null or empty");
    }

    @Test
    @DisplayName("Ошибка создания/обновления - неверно заполненные поля")
    void saveBook_FailOnUserTest() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(person.getId());
        bookDto.setTitle("Mummy");
        bookDto.setAuthor("Aaron");
        bookDto.setPageCount(500);

        //when
        when(userRepository.findById(person.getId())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(()->bookService.createBook(bookDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User with such id not found");

    }

    @Test
    @DisplayName("Ошибка получения - нет книги в БД")
    void getOneBook_FailTest() {
        //given
        Long bookId = 1L;

        //when
        when(bookRepository.existsById(bookId)).thenReturn(false);

        //then
        assertThatThrownBy(()->bookService.getBookById(bookId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Book was not found");
    }

    @Test
    @DisplayName("Ошибка удаления - нет книги в БД")
    void deleteOneBook_FailTest() {
        //given
        Long bookId = 1L;

        //when
        when(bookRepository.existsById(bookId)).thenReturn(false);

        //then
        assertThatThrownBy(()->bookService.deleteBookById(bookId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No book with requested id to delete");
    }
}
