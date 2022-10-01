package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotUniqueException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson  = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.existsByTitle(person.getTitle())).thenReturn(false);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    // update
    @Test
    @DisplayName("Обновление пользователя")
    void updatePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person existsPerson  = new Person();
        existsPerson.setFullName("test");
        existsPerson.setAge(12);
        existsPerson.setTitle("test title");

        Person updatedPerson  = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setFullName("test name");
        updatedPerson.setAge(11);
        updatedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.existsByTitle(person.getTitle())).thenReturn(false);
        when(userRepository.getByTitle(person.getTitle())).thenReturn(Optional.of(existsPerson));
        when(userRepository.save(person)).thenReturn(updatedPerson);
        when(userMapper.personToUserDto(updatedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals(1L, userDtoResult.getId());
        assertEquals(11, userDtoResult.getAge());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
    }

    // get
    @Test
    @DisplayName("Получение пользователя")
    void getPerson_Test() {
        //given

        Long userId = 1L;

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person gotPerson  = new Person();
        gotPerson.setId(1L);
        gotPerson.setFullName("test name");
        gotPerson.setAge(11);
        gotPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(gotPerson));
        when(userMapper.personToUserDto(gotPerson)).thenReturn(result);


        //then
        UserDto userDtoResult = userService.getUserById(userId);
        assertEquals(1L, userDtoResult.getId());
        assertEquals("test name", userDtoResult.getFullName());
    }

    // delete
    @Test
    @DisplayName("Удаление пользователя")
    void deletePerson_Test() {
        //given
        Long userId = 1L;

        //when
        when(userRepository.existsById(userId)).thenReturn(true);

        //then
        userService.deleteUserById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    // * failed
    //         doThrow(dataInvalidException).when(testRepository)
    //                .save(same(test));
    // example failed
    //  assertThatThrownBy(() -> testeService.createTest(testRequest))
    //                .isInstanceOf(DataInvalidException.class)
    //                .hasMessage("Invalid data set");


    @Test
    @DisplayName("Ошибка создания/обновления - неверно заполненные поля")
    void savePerson_FailOnFieldsTest() {
        //given

        UserDto userDto1 = new UserDto();
        userDto1.setAge(11);
        userDto1.setTitle("test title");

        UserDto userDto2 = new UserDto();
        userDto2.setAge(11);
        userDto2.setFullName("test name");

        UserDto userDto3 = new UserDto();
        userDto3.setFullName("test name");
        userDto3.setTitle("test title");

        Person person1  = new Person();
        person1.setAge(11);
        person1.setTitle("test title");

        Person person2  = new Person();
        person2.setAge(11);
        person2.setTitle("test title");

        Person person3  = new Person();
        person3.setFullName("test name");
        person3.setTitle("test title");

        //when
        when(userMapper.userDtoToPerson(userDto1)).thenReturn(person1);
        when(userMapper.userDtoToPerson(userDto2)).thenReturn(person2);
        when(userMapper.userDtoToPerson(userDto3)).thenReturn(person3);

        //then
        assertThatThrownBy(()->userService.createUser(userDto1))
                .isInstanceOf(EmptyFieldException.class)
                .hasMessage("User fields cannot be equals null or empty");
        assertThatThrownBy(()->userService.createUser(userDto2))
                .isInstanceOf(EmptyFieldException.class)
                .hasMessage("User fields cannot be equals null or empty");
        assertThatThrownBy(()->userService.createUser(userDto3))
                .isInstanceOf(EmptyFieldException.class)
                .hasMessage("User fields cannot be equals null or empty");
    }

    @Test
    @DisplayName("Ошибка создания - не уникальное поле title")
    void savePerson_FailOnTitleTest() {
        //given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person  = new Person();
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        //when
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.existsByTitle(person.getTitle())).thenReturn(true);

        //then
        assertThatThrownBy(()->userService.createUser(userDto))
                .isInstanceOf(NotUniqueException.class)
                .hasMessage("User title is not unique");
    }

    @Test
    @DisplayName("Ошибка получения - нет пользователя в БД")
    void getPerson_FailTest() {
        //given
        Long userId = 1L;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(()->userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User was not found.");
    }

    @Test
    @DisplayName("Ошибка удаления - нет пользователя в БД")
    void deletePerson_FailTest() {
        //given
        Long userId = 1L;

        //when
        when(userRepository.existsById(userId)).thenReturn(false);

        //then
        assertThatThrownBy(()->userService.deleteUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with requested id to delete");
    }
}
