package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    @Transactional
    UserDto createUser(NewUserRequest request);

    UserDto getUserById(long userId);

    List<UserDto> getUsers();

    @Transactional
    UserDto updateUser(long userId, UpdateUserRequest request);

    @Transactional
    void deleteUser(long userId);

    User validateUserExist(Long userId);
}