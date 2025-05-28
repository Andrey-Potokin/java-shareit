package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static void updateUserFields(User user, UpdateUserDto requestUserDto) {
        if (requestUserDto.hasEmail()) {
            user.setEmail(requestUserDto.getEmail());
        }
        if (requestUserDto.hasName()) {
            user.setName(requestUserDto.getName());
        }
    }

    public static User mapToUser(NewUserDto requestUserDto) {
        return User.builder()
                .name(requestUserDto.getName())
                .email(requestUserDto.getEmail())
                .build();
    }
}