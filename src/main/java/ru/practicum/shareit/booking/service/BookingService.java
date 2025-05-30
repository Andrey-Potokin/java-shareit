package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, NewBookingRequest request);

    BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getBookingsByBookerId(Long userId, String state);

    List<BookingDto> getBookingsByOwnerId(Long userId, String state);
}