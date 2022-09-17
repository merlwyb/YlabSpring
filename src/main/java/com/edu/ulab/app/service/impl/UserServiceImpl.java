package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return userRepository.create(userDto);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        return userRepository.update(userDto);
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean userIsPresent(Long id) {
        return userRepository.isPresent(id);
    }
}
