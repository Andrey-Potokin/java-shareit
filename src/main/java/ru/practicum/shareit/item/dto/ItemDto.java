package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String name;
    private String description;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long ownerId;
    private Boolean available;
}