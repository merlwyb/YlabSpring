package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User userToCreate = userMapper.userDtoToUser(userDto);
        return userMapper.userToUserDto(userRepository.create(userToCreate));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User userToUpdate = userMapper.userDtoToUser(userDto);
        return userMapper.userToUserDto(userRepository.update(userToUpdate));
    }

    @Override
    public UserDto getUserById(Long id) {
        return userMapper.userToUserDto(userRepository.getById(id));
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
