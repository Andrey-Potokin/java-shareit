package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @NotNull(message = "Email не должен быть пустым")
    @Email(message = "Некорректный email")
    private String email;
}
