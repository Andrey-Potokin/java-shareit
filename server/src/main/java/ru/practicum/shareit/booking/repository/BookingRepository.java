package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByStatus(BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1")
    List<Booking> findAllByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 AND b.status = ?2")
    List<Booking> findAllByBookerIdAndStatus(Long userId, BookingStatus bookingStatus);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1")
    List<Booking> findAllByOwnerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = ?1 AND b.status = ?2")
    List<Booking> findAllByOwnerIdAndStatus(Long userId, BookingStatus bookingStatus);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sortOrder);
}