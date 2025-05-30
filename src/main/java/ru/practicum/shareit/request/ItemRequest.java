package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "requests")
public class ItemRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "description")
    private String description;

    @Column(name = "requestor")
    private String requestor; // пользователь, создающий запрос

    @Column(name = "created")
    private LocalDateTime created; // дата и время создания запроса
}