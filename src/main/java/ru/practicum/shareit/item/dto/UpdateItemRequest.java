package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class UpdateItemRequest {
    public String name;
    public String description;
    public Boolean available;

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