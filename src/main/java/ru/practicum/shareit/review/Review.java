package ru.practicum.shareit.review;

import lombok.Data;

@Data
public class Review {
    private long reviewId;
    private String description;
    private long userId; // Идентификатор пользователя, оставившего отзыв
    private long itemId;
}