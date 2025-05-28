package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    @Transactional
    BookingDto create(Long userId, NewBookingDto newBookingDto);

    BookingDto findById(Long userId, Long bookingId);

    @Transactional
    BookingDto updateStatus(Long userId, Long bookingId, boolean approved);

    List<BookingDto> findAllByBookerId(Long bookerId, BookingState state);

    List<BookingDto> findAllByOwnerId(Long ownerId, BookingState state);
}