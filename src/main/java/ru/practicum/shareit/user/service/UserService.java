package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest request);

    UserDto getUserById(long userId);

    List<UserDto> getUsers();

    UserDto updateUser(long userId, UpdateUserRequest request);

    void deleteUser(long userId);
}