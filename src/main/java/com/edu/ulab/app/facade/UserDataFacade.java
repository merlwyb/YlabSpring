package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.RepositoryException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import com.edu.ulab.app.web.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserService userService,
                          BookService bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {

        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        try {
            List<Long> bookIdList = userBookRequest.getBookRequests()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookRequestToBookDto)
                    .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                    .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                    .map(bookService::createBook)
                    .peek(createdBook -> log.info("Created book: {}", createdBook))
                    .map(BookDto::getId)
                    .toList();
            log.info("Collected book ids: {}", bookIdList);

            return UserBookResponse.builder()
                    .userId(createdUser.getId())
                    .booksIdList(bookIdList)
                    .build();
        } catch (RepositoryException e) {
            bookService.deleteAllBooksByUserId(createdUser.getId());
            userService.deleteUserById(createdUser.getId());
            throw new RepositoryException(e.getMessage());
        }
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest, Long userId) {
        log.info("Got user book with userID={} update request: {}", userId, userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        userDto.setId(userId);
        log.info("Mapped user request: {}", userDto);

        boolean userExistsBeforeUpdate = userService.userIsPresent(userId);

        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);

        List<Long> temporaryIds = new ArrayList<>();
        try{
            List<Long> bookIdList = userBookRequest.getBookRequests()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookRequestToBookDto)
                    .peek(bookDto -> bookDto.setUserId(updatedUser.getId()))
                    .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                    .map(bookService::updateBook)
                    .peek(updatedBook -> {
                        log.info("Updated book: {}", updatedBook);
                        temporaryIds.add(updatedBook.getId());
                    })
                    .map(BookDto::getId)
                    .toList();
            log.info("Updated books ids: {}", bookIdList);

            List<Long> allBooksIdList = bookService.getAllBooksByUserId(updatedUser.getId()).stream().map(BookDto::getId).toList();
            log.info("All books ids: {}", allBooksIdList);

            return UserBookResponse.builder()
                    .userId(updatedUser.getId())
                    .booksIdList(allBooksIdList)
                    .build();
        } catch (RepositoryException e) {
            temporaryIds.forEach(bookService::deleteBookById);
            if (!userExistsBeforeUpdate)
                userService.deleteUserById(userId);
            throw new RepositoryException(e.getMessage());
        }

    }

    public UserResponse getUserWithBooks(Long userId) {
        UserDto gotUser = userService.getUserById(userId);
        log.info("Got user: {}", gotUser);

        List<BookDto> booksList = bookService.getAllBooksByUserId(gotUser.getId()).stream().toList();
        log.info("All user books: {}", booksList);

        return UserResponse.builder()
                .userId(gotUser.getId())
                .fullName(gotUser.getFullName())
                .title(gotUser.getTitle())
                .age(gotUser.getAge())
                .booksList(booksList)
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        userService.deleteUserById(userId);
        log.info("Delete user with id: {}", userId);
    }
}
