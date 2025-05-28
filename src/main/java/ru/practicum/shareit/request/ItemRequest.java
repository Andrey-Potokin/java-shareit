package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@Builder
public class ItemRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;

    private String requestor; // пользователь, создающий запрос

    private LocalDateTime created; // дата и время создания запроса
}