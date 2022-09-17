package com.edu.ulab.app.repository;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NoSuchEntityException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {

    private long id = 1;
    private final Map<Long, User> userMap = new HashMap<>();

    public UserDto create(UserDto userDto) {
        checkOnNullAndEmptyValues(userDto);
        User user = new User(userDto.getFullName(), userDto.getTitle(), userDto.getAge());
        userMap.put(id, user);
        userDto.setId(id++);

        return userDto;
    }

    public UserDto update(UserDto userDto) {
        if (userDto.getId() != null) {
            checkOnNullAndEmptyValues(userDto);
            User user = new User(userDto.getFullName(), userDto.getTitle(), userDto.getAge());
            userMap.put(userDto.getId(), user);
        } else {
            return create(userDto);
        }
        return userDto;
    }

    public UserDto getById(Long id) {
        User user = userMap.get(id);
        if (user == null) {
            throw new NoSuchEntityException("User was not found with id = " + id);
        }

        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFullName(user.getFullName());
        userDto.setTitle(user.getTitle());
        userDto.setAge(user.getAge());

        return userDto;
    }

    public void deleteById(Long id) {
        userMap.remove(id);
    }

    public boolean isPresent(Long id) {
        return userMap.containsKey(id);
    }

    private void checkOnNullAndEmptyValues(UserDto userDto) {
        if (userDto.getFullName() == null ||
                userDto.getFullName().matches("^\s*$") ||
                userDto.getTitle() == null ||
                userDto.getTitle().matches("^\s*$") ||
                userDto.getAge() <= 0)
            throw new EmptyFieldException("User cannot have an empty or null values");
    }


}
