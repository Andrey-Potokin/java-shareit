package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User insert(User user);

    Optional<User> findById(long userId);

    List<User> findUsers();

    void update(long userId, User updatedUser);

    void deleteById(long userId);

    Optional<User> findByEmail(String email);
}