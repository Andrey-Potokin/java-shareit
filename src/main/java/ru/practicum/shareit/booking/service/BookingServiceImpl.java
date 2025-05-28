package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto create(Long userId, NewBookingDto newBookingDto) {
        validateDate(newBookingDto);
        User booker = userService.validateUserExist(userId);
        Item item = validateItemExist(newBookingDto.getItemId());

        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь не доступна для бронирования!");
        }

        Booking booking = BookingMapper.mapToNewBooking(newBookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        return BookingMapper.mapToBookingDto(validateBooking(userId,bookingId));
    }

    @Override
    public BookingDto updateStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = validateBookingExist(bookingId);
        Item item = booking.getItem();
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Данная вещь не принадлежит этому пользователю");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> findAllByBookerId(Long bookerId, BookingState state) {
        userService.validateUserExist(bookerId);

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case BookingState.WAITING: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStatus(bookerId,
                        BookingStatus.WAITING, sortOrder));
                break;
            }
            case BookingState.REJECTED: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStatusIn(bookerId,
                        List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), sortOrder));
                break;
            }
            case BookingState.CURRENT: {
                bookings = new ArrayList<>(
                        bookingRepository.findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(bookerId, now,
                                now, sortOrder));
                break;
            }
            case BookingState.FUTURE: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndStartAfter(bookerId,
                        now, sortOrder));
                break;
            }
            case BookingState.PAST: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerIdAndEndBefore(bookerId,
                        now, sortOrder));
                break;
            }
            case BookingState.ALL: {
                bookings = new ArrayList<>(bookingRepository.findAllByBookerId(bookerId, sortOrder));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> findAllByOwnerId(Long ownerId, BookingState state) {
        userService.validateUserExist(ownerId);

        List<Item> userItemsIds = itemRepository.findByOwnerIdOrderByIdAsc(ownerId);

        if (userItemsIds.isEmpty()) {
            throw new ValidationException("Этот запрос только для тех пользователей, которые имеют хотя бы 1 вещь");
        }

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        Sort sortOrder = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case BookingState.WAITING: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId,
                        BookingStatus.WAITING, sortOrder));
                break;
            }
            case BookingState.REJECTED: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId,
                        BookingStatus.REJECTED, sortOrder));
                break;
            }
            case BookingState.CURRENT: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(ownerId,
                        now, now, sortOrder));
                break;
            }
            case BookingState.FUTURE: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId,
                        now, sortOrder));
                break;
            }
            case BookingState.PAST: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId,
                        now, sortOrder));
                break;
            }
            case BookingState.ALL: {
                bookings = new ArrayList<>(bookingRepository.findAllByItemOwnerId(ownerId, sortOrder));
                break;
            }
            default:
                bookings = new ArrayList<>();
        }
        return bookings.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    private void validateDate(NewBookingDto bookingDto) {
        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("Неверные даты начала и окончания бронирования");
        }
    }

    private Booking validateBooking(Long userId, Long bookingId) {
        User user = userService.validateUserExist(userId);
        Booking booking = validateBookingExist(bookingId);
        Item item = booking.getItem();

        if (!booking.getBooker().equals(user) && !item.getOwner().equals(user)) {
            throw new ForbiddenException("Данная бронь не имеет отношения к пользователю");
        }

        return booking;
    }

    private Booking validateBookingExist(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id %d не найдено.", bookingId)));

    }

    private Item validateItemExist(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь для бронирования с id %d не найдена.",
                        itemId)));
    }
}