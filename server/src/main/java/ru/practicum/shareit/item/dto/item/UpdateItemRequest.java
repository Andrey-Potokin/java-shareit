package ru.practicum.shareit.item.dto.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateItemRequest {
    public Long id;
    public String name;
    public String description;
    public Boolean available;
    public long requestId;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return ! (available == null);
    }
}