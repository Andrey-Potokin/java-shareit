package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {
    @Autowired
    UserService userService;

    NewUserRequest user1;
    NewUserRequest user2;

    @BeforeAll
    void beforeAll() {
        user1 = NewUserRequest.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = NewUserRequest.builder().name("Yandex2").email("yandex2@practicum.ru").build();
    }

    @Test
    void getAllUsers() {
        userService.createUser(user1);
        UserDto newUser = userService.createUser(user2);
        List<UserDto> users = userService.getUsers().stream().toList();

        assertThat(users.get(1).getId()).isEqualTo(newUser.getId());
        assertThat(users.get(1).getName()).isEqualTo(newUser.getName());
        assertThat(users.get(1).getEmail()).isEqualTo(newUser.getEmail());
    }

    @Test
    void createAndGetUser() {
        UserDto user = userService.createUser(user1);
        UserDto getUser = userService.getUserById(user.getId());

        assertThat(user.getId()).isEqualTo(getUser.getId());
        assertThat(user.getName()).isEqualTo(getUser.getName());
        assertThat(user.getEmail()).isEqualTo(getUser.getEmail());
    }

    @Test
    void throwExceptionWhenEmailIsDuplicateWhenCreateUser() {
        userService.createUser(user1);
        NewUserRequest newUser = NewUserRequest.builder().name("Yandex2").email("yandex@practicum.ru").build();

        assertThatThrownBy(() -> userService.createUser(newUser)).isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        assertThatThrownBy(() -> userService.getUserById(null)).isInstanceOf(
                InvalidDataAccessApiUsageException.class);
    }

    @Test
    void updateUser() {
        UserDto user = userService.createUser(user1);
        UpdateUserRequest updateUserDto = UpdateUserRequest.builder().name(user2.getName()).email(user2.getEmail()).build();
        UserDto updateUser = userService.updateUser(user.getId(), updateUserDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user2.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user2.getEmail());
    }

    @Test
    void updateUserNameIsNull() {
        UserDto user = userService.createUser(user1);
        UpdateUserRequest userUpdateDto = UpdateUserRequest.builder().email("yandex2@practicum.ru").build();
        UserDto updateUser = userService.updateUser(user.getId(), userUpdateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(user.getName());
        assertThat(updateUser.getEmail()).isEqualTo(userUpdateDto.getEmail());
    }

    @Test
    void updateUserEmailIsNull() {
        UserDto user = userService.createUser(user1);
        UpdateUserRequest userUpdateDto = UpdateUserRequest.builder().name("Yandex2").build();
        UserDto updateUser = userService.updateUser(user.getId(), userUpdateDto);

        assertThat(updateUser.getId()).isEqualTo(user.getId());
        assertThat(updateUser.getName()).isEqualTo(userUpdateDto.getName());
        assertThat(updateUser.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void throwExceptionWhenEmailIsDuplicateWhenUpdateUser() {
        UserDto user = userService.createUser(user1);
        UserDto user3 = userService.createUser(user2);
        UpdateUserRequest updateUser = UpdateUserRequest.builder().name("Yandex2").email("yandex@practicum.ru").build();

        assertThatThrownBy(() -> userService.updateUser(user3.getId(), updateUser))
                .isInstanceOf(DuplicatedDataException.class);
    }

    @Test
    void deleteUser() {
        UserDto user = userService.createUser(user1);
        userService.deleteUser(user.getId());

        assertThatThrownBy(() -> userService.getUserById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}