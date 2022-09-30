package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.NotUniqueException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);

        checkOnNullAndEmptyValues(user);
        if (userRepository.existsByTitle(user.getTitle()))
            throw new NotUniqueException("User title is not unique");

        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);

        checkOnNullAndEmptyValues(user);
        if (userRepository.existsByTitle(user.getTitle()))
            if (!userRepository.getByTitle(user.getTitle()).get().getId().equals(user.getId()))
                throw new NotUniqueException("User title is not unique");

        Person updatedUser = userRepository.save(user);
        log.info("Updated user: {}", updatedUser);
        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Trying to find user with id: {}", id);
        Person gotUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User was not found."));
        log.info("Got user: {}", gotUser);
        return userMapper.personToUserDto(gotUser);
    }

    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id))
            throw new NotFoundException("No user with requested id to delete");

        log.info("Trying to delete user with id: {}", id);
        userRepository.deleteById(id);
        log.info("User was deleted with id: {}", id);
    }

    private void checkOnNullAndEmptyValues(Person user) {
        if (user.getFullName() == null ||
                user.getFullName().matches("^\s*$") ||
                user.getTitle() == null ||
                user.getTitle().matches("^\s*$") ||
                user.getAge() <= 0)
            throw new EmptyFieldException("User fields cannot be equals null or empty");
    }

    private void checkOnUniqueTitle(String userTitle) {
        if (userRepository.existsByTitle(userTitle))
            throw new NotUniqueException("User title is not unique");
    }
}
