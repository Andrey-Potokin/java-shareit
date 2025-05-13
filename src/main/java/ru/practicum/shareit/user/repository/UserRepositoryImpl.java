//package ru.practicum.shareit.user.repository;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.exception.NotFoundException;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//@Slf4j
//public class UserRepositoryImpl implements UserRepository {
//    private final List<User> users = new ArrayList<>();
//
//    @Override
//    public User insert(User newUser) {
//        newUser.setId(getNextId());
//        users.add(newUser);
//        log.info("Создан новый пользователь: {}", newUser);
//        return newUser;
//    }
//
//    @Override
//    public Optional<User> findById(long userId) {
//       Optional<User> userOpt = users.stream()
//                .filter(user -> user.getId() == userId)
//                .findFirst();
//       log.info("Найден пользователь с id: {}", userId);
//       return userOpt;
//    }
//
//    @Override
//    public List<User> findUsers() {
//        log.info("Получены все пользователи.");
//        return users;
//    }
//
//    @Override
//    public void update(long userId, User updatedUser) {
//        int index = users.indexOf(findById(userId)
//                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден")));
//        users.set(index, updatedUser);
//        log.info("Обновлен пользователь: {}", updatedUser);
//    }
//
//    @Override
//    public void deleteById(long userId) {
//        users.removeIf(user -> user.getId() == userId);
//        log.info("Удален пользователь с id: {}", userId);
//    }
//
//    @Override
//    public Optional<User> findByEmail(String email) {
//        Optional<User> userOpt = users.stream()
//                .filter(user -> user.getEmail().equals(email))
//                .findFirst();
//        log.info("Найден пользователь с email: {}", email);
//        return userOpt;
//    }
//
//    private long getNextId() {
//        long currentMaxId = users.stream()
//                .mapToLong(User::getId)
//                .max()
//                .orElse(0);
//        return ++currentMaxId;
//    }
//}