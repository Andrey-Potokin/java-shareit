package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private long itemId;
    private long bookerId;
}