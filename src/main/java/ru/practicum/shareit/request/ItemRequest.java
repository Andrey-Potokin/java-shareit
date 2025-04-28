package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private long id;
    private String description;
    private String requestor; // пользователь, создающий запрос
    private LocalDateTime created; // дата и время создания запроса
}