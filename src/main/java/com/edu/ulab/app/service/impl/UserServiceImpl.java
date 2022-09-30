package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NoSuchEntityException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);

        checkOnNullAndEmptyValues(user);

        Person updatedUser;
        Optional<Person> userForUpdate = userRepository.findByIdForUpdate(user.getId());
        if (userForUpdate.isPresent()) {
            updatedUser = userForUpdate.get();

            updatedUser.setFullName(user.getFullName());
            updatedUser.setTitle(user.getTitle());
            updatedUser.setAge(user.getAge());
        } else {
            updatedUser = user;
        }

        updatedUser = userRepository.save(updatedUser);
        log.info("Updated user: {}", updatedUser);

        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        Person gotUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User was not found."));
        log.info("Got user: {}", gotUser);
        return userMapper.personToUserDto(gotUser);
    }


    @Override
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id))
            throw new NoSuchEntityException("No user with requested id to delete");

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
}
