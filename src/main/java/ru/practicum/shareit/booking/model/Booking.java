package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private long itemId;
    private long bookerId;
}