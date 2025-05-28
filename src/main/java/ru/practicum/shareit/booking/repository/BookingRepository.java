package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sortOrder);

    List<Booking> findAllByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sortOrder);

    List<Booking> findAllByItemOwnerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long ownerId, LocalDateTime now1,
                                                                                  LocalDateTime now2, Sort sortOrder);

    List<Booking> findAllByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sortOrder);

    List<Booking> findAllByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sortOrder);

    List<Booking> findAllByBookerId(Long bookerId, Sort sortOrder);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sortOrder);

    List<Booking> findAllByBookerIdAndStatusIn(Long bookerId, List<BookingStatus> statuses, Sort sortOrder);

    List<Booking> findAllByBookerIdAndStartLessThanEqualAndEndGreaterThanEqual(Long bookerId, LocalDateTime now1,
                                                                               LocalDateTime now2, Sort sortOrder);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sortOrder);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sortOrder);

    List<Booking> findAllByItemId(Long itemId);
}