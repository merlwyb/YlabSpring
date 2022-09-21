package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {

    private long id = 1;
    private final Map<Long, User> userMap = new HashMap<>();

    public User create(User user) {
        if (checkOnNullAndEmptyValues(user))
            return new User();
        user.setId(id);
        userMap.put(id++, user);
        return user;
    }

    public User update(User user) {
        if (user.getId() != null) {
            if (checkOnNullAndEmptyValues(user))
                return new User();
            userMap.put(user.getId(), user);
        } else {
            return create(user);
        }
        return user;
    }

    public User getById(Long id) {
        User user = userMap.get(id);
        if (user == null) {
            return new User();
        }
        return user;
    }

    public void deleteById(Long id) {
        userMap.remove(id);
    }

    public boolean isPresent(Long id) {
        return userMap.containsKey(id);
    }

    private boolean checkOnNullAndEmptyValues(User user) {
        return user.getFullName() == null ||
                user.getFullName().matches("^\s*$") ||
                user.getTitle() == null ||
                user.getTitle().matches("^\s*$") ||
                user.getAge() <= 0;
    }


}
