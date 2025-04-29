package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest request) {
        checkEmailUniqueness(request.getEmail());
        User user = UserMapper.mapToUser(request);
        userRepository.insert(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findUsers().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto updateUser(long userId, UpdateUserRequest request) {
        checkEmailUniqueness(request.getEmail());
        User updatedUser = userRepository.findById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        userRepository.update(userId,updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicatedDataException("Пользователь с таким email уже существует");
        }
    }
}