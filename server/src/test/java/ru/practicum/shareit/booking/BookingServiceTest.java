package ru.practicum.shareit.booking;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.ParameterNotValidException;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingServiceTest {
    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    NewUserRequest user1;
    NewUserRequest user2;
    NewItemRequest item1;

    @BeforeAll
    void beforeAll() {
        user1 = NewUserRequest.builder().name("Yandex").email("yandex@practicum.ru").build();
        user2 = NewUserRequest.builder().name("Yandex2").email("yandex2@practicum.ru").build();
        item1 = NewItemRequest.builder().name("Yandex").description("YandexPracticum").available(true).build();
    }

    @Test
    void create() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);

        assertThat(newBooking.getId()).isNotNull();
        assertThat(newBooking.getStart()).isEqualTo(booking.getStart());
        assertThat(newBooking.getEnd()).isEqualTo(booking.getEnd());
        assertThat(newBooking.getItem().getId()).isEqualTo(booking.getItemId());
        assertThat(newBooking.getBooker().getId()).isEqualTo(user4.getId());
        assertThat(newBooking.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void throwExceptionWhenIdIsNull() {
        UserDto user3 = userService.createUser(user1);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        assertThatThrownBy(() -> bookingService.createBooking(null, booking))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    void throwExceptionWhenItemIsNotAvailable() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        NewItemRequest item2 = NewItemRequest.builder()
                .name("Yandex")
                .description("YandexPracticum")
                .available(false)
                .build();
        ItemDto item = itemService.create(user3.getId(), item2);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(user4.getId(), booking))
                .isInstanceOf(ItemNotAvailableException.class);
    }

    @Test
    void throwExceptionWhenBookingStartEqualToEnd() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(1))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(user4.getId(), booking))
                .isInstanceOf(ParameterNotValidException.class);
    }

    @Test
    void updateStatusBooking() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        BookingDto approvedBooking = bookingService.updateBookingStatus(user3.getId(), newBooking.getId(), true);

        assertThat(approvedBooking.getId()).isEqualTo(newBooking.getId());
        assertThat(approvedBooking.getStart()).isEqualTo(newBooking.getStart());
        assertThat(approvedBooking.getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(approvedBooking.getItem()).isEqualTo(newBooking.getItem());
        assertThat(approvedBooking.getBooker()).isEqualTo(user4);
        assertThat(approvedBooking.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void findById() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto bookingDto = bookingService.createBooking(user4.getId(), booking);
        BookingDto getBooking = bookingService.getBooking(user4.getId(), bookingDto.getId());
        BookingDto getBookingOwner = bookingService.getBooking(user3.getId(), bookingDto.getId());

        assertThat(getBooking.getId()).isEqualTo(bookingDto.getId());
        assertThat(getBooking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(getBooking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(getBooking.getItem()).isEqualTo(bookingDto.getItem());
        assertThat(getBooking.getBooker()).isEqualTo(user4);
        assertThat(getBooking.getStatus()).isEqualTo(BookingStatus.WAITING);

        assertThat(getBookingOwner.getId()).isEqualTo(bookingDto.getId());
        assertThat(getBookingOwner.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(getBookingOwner.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(getBookingOwner.getItem()).isEqualTo(bookingDto.getItem());
        assertThat(getBookingOwner.getBooker()).isEqualTo(user4);
        assertThat(getBookingOwner.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByUser() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.getBookingsByBookerId(user4.getId(), "ALL").stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void getBookingsByOwner() {
        UserDto user3 = userService.createUser(user1);
        UserDto user4 = userService.createUser(user2);
        ItemDto item = itemService.create(user3.getId(), item1);
        NewBookingRequest booking = NewBookingRequest.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        BookingDto newBooking = bookingService.createBooking(user4.getId(), booking);
        List<BookingDto> bookings = bookingService.getBookingsByOwnerId(user3.getId(), "ALL").stream().toList();

        assertThat(bookings.getFirst().getId()).isEqualTo(newBooking.getId());
        assertThat(bookings.getFirst().getStart()).isEqualTo(newBooking.getStart());
        assertThat(bookings.getFirst().getEnd()).isEqualTo(newBooking.getEnd());
        assertThat(bookings.getFirst().getItem()).isEqualTo(newBooking.getItem());
        assertThat(bookings.getFirst().getBooker()).isEqualTo(user4);
        assertThat(bookings.getFirst().getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}