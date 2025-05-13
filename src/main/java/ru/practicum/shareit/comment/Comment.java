package ru.practicum.shareit.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Comment {
    private long reviewId;
    private String description;
    private long userId; // Идентификатор пользователя, оставившего отзыв
    private long itemId;
}