package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "Необходимо указать имя предмета")
    private String name;
    @NotBlank(message = "Необходимо указать описание предмета")
    private String description;
    @NotNull(message = "Необходимо указать статус предмета")
    private Boolean available;
}
