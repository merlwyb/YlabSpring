package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.EmptyFieldException;
import com.edu.ulab.app.exception.NoSuchEntityException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
    final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
    final String SELECT_SQL = "SELECT * FROM PERSON WHERE ID = ?";
    final String DELETE_SQL = "DELETE FROM PERSON WHERE ID = ?";

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        checkOnNullAndEmptyValues(userDto);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        checkOnNullAndEmptyValues(userDto);

        boolean notSucceed = jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(),
                userDto.getTitle(), userDto.getAge(), userDto.getId()) == 0;

        if (notSucceed)
            return createUser(userDto);

        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        Person gotUser = jdbcTemplate.query(SELECT_SQL, rs -> {
            if (rs.next()) {
                Person person = new Person();
                person.setId(rs.getLong("ID"));
                person.setFullName(rs.getString("FULL_NAME"));
                person.setTitle(rs.getString("TITLE"));
                person.setAge(rs.getInt("AGE"));
                return person;
            }
            return null;
        }, id);

        if (gotUser == null)
            throw new NoSuchEntityException("No user with requested id");
        return userMapper.personToUserDto(gotUser);
    }

    @Override
    public void deleteUserById(Long id) {
        if (jdbcTemplate.update(DELETE_SQL, id) == 0)
            throw new NoSuchEntityException("No user with requested id to delete");
    }

    private void checkOnNullAndEmptyValues(UserDto userDto) {
        if (userDto.getFullName() == null ||
                userDto.getFullName().matches("^\s*$") ||
                userDto.getTitle() == null ||
                userDto.getTitle().matches("^\s*$") ||
                userDto.getAge() <= 0)
            throw new EmptyFieldException("User fields cannot be equals null or empty");
    }
}
