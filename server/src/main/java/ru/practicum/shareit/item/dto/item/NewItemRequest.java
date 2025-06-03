package ru.practicum.shareit.item.dto.item;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewItemRequest {
    String name;
    String description;
    Boolean available;
    Long requestId;
}