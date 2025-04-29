package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private long itemId;
    private long bookerId;
    private Status status;
}