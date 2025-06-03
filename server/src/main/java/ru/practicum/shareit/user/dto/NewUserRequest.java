package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NewUserRequest {
    private String name;
    private String description;
    private String email;
    private Boolean available;
}