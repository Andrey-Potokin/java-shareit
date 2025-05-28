package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ParameterNotValidException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto createBooking(Long bookerId, NewBookingRequest request) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", bookerId);
                    return new NotFoundException("Пользователь с id " + bookerId + " не найден");
                });
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> {
                    log.error("Вещь с id {} не найдена", request.getItemId());
                    return new NotFoundException("Вещь с id " + request.getItemId() + " не найдена");
                });
        if (request.getStart().isBefore(request.getEnd()) && !request.getStart().equals(request.getEnd())) {
            if (item.getAvailable()) {
                Booking booking = BookingMapper.toBooking(request);
                booking.setItem(item);
                booking.setBooker(booker);
                booking.setStatus(BookingStatus.WAITING);
                bookingRepository.save(booking);
                log.debug("Бронирование создано: {}", booking);
                BookingDto bookingDto = BookingMapper.toBookingDto(booking);
                log.debug("Возвращено DTO: {}", bookingDto);
                return bookingDto;
            } else {
                log.error("Вещь с id {} не доступна для бронирования", request.getItemId());
                throw new ItemNotAvailableException("Вещь с id " + request.getItemId() + " не доступна для бронирования");
            }
        } else {
            log.error("Дата начала бронирования должна быть раньше даты окончания и не может быть равна ей");
            throw new ParameterNotValidException("Дата начала бронирования должна быть раньше даты окончания и не может быть равна ей");
        }
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с id {} не найдено", bookingId);
                    return new NotFoundException("Бронирование с id " + bookingId + " не найдено");
                });
        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            log.error("Пользователь с id {} не является владельцем вещи с id {}", userId, booking.getItem().getId());
            throw new NotFoundException("Пользователь с id " + userId + " не является владельцем вещи с id "
                    + booking.getItem().getId());
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByBookerId(Long bookerId, String state) {
        if (!state.equals("ALL") && !state.equals("CURRENT") && !state.equals("FUTURE")
                && !state.equals("PAST") && !state.equals("WAITING") && !state.equals("REJECTED")) {
            log.error("Неверное состояние бронирования");
            throw new NotFoundException("Неверное состояние бронирования");
        }

        if (state.equals("ALL")) {
            return bookingRepository.findAllByBookerId(bookerId).stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .map(BookingMapper::toBookingDto)
                    .toList();
        } else {
            return bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.valueOf(state.toUpperCase()))
                    .stream()
                    .sorted(Comparator.comparing(Booking::getStart).reversed())
                    .map(BookingMapper::toBookingDto)
                    .toList();
        }
    }

    @Override
    public List<BookingDto> getBookingsByOwnerId(Long ownerId, String state) {
        if (!state.equals("ALL") && !state.equals("CURRENT") && !state.equals("FUTURE")
                && !state.equals("PAST") && !state.equals("WAITING") && !state.equals("REJECTED")) {
            log.error("Неверное состояние бронирования");
            throw new NotFoundException("Неверное состояние бронирования");
        }

        List<Booking> bookings;
        if (state.equals("ALL")) {
            bookings = bookingRepository.findAllByOwnerId(ownerId);
        } else {
            bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.valueOf(state.toUpperCase()));
        }

        if (bookings.isEmpty()) {
            log.error("Список бронирований пуст");
            throw new EmptyResultDataAccessException("Список бронирований пуст", 1);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public BookingDto updateBookingStatus(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с id {} не найдено", bookingId);
                    return new NotFoundException("Бронирование с id " + bookingId + " не найдено");
                });

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.error("Пользователь с id {} не является владельцем вещи с id {}", ownerId, booking.getItem().getId());
            throw new NotOwnerException("Пользователь с id " + ownerId + " не является владельцем вещи с id "
                    + booking.getItem().getId());
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }
}