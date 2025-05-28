package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    List<UserDto> findAll();

    @Transactional
    UserDto create(NewUserDto userDto);

    UserDto findById(Long userId);

    @Transactional
    UserDto update(Long userId, UpdateUserDto userDto);

    @Transactional
    void deleteById(Long userId);

    User validateUserExist(Long userId);
}