package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    NewBookingRequest newBookingRequest;
    BookingDto booking2;
    BookingDto booking3;

    @BeforeEach
    void beforeEach() {
        newBookingRequest = NewBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();
        UserDto user = UserDto.builder().id(1L).name("Yandex").email("yandex@practicum.ru").build();
        ItemDto item = ItemDto.builder().id(1L).name("Yandex").description("YandexPracticum").available(true).build();
        booking2 = BookingDto.builder()
                .id(1L)
                .start(newBookingRequest.getStart())
                .end(newBookingRequest.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        booking3 = BookingDto.builder()
                .id(1L)
                .start(newBookingRequest.getStart())
                .end(newBookingRequest.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.createBooking(1L, newBookingRequest)).thenReturn(booking2);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Yandex"))
                .andExpect(jsonPath("$.item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("Yandex"))
                .andExpect(jsonPath("$.booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void updateStatusBooking() throws Exception {
        when(bookingService.updateBookingStatus(1L, 1L, true)).thenReturn(booking3);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Yandex"))
                .andExpect(jsonPath("$.item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("Yandex"))
                .andExpect(jsonPath("$.booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBooking(1L, 1L)).thenReturn(booking2);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("Yandex"))
                .andExpect(jsonPath("$.item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("Yandex"))
                .andExpect(jsonPath("$.booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getAllBookingsForBooker() throws Exception {
        List<BookingDto> bookings = List.of(booking2);

        when(bookingService.getBookingsByBookerId(1L, "ALL")).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Yandex"))
                .andExpect(jsonPath("$[0].item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].booker.name").value("Yandex"))
                .andExpect(jsonPath("$[0].booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllBookingsForOwner() throws Exception {
        List<BookingDto> bookings = List.of(booking2);

        when(bookingService.getBookingsByOwnerId(1L, "ALL")).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("Yandex"))
                .andExpect(jsonPath("$[0].item.description").value("YandexPracticum"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].booker.name").value("Yandex"))
                .andExpect(jsonPath("$[0].booker.email").value("yandex@practicum.ru"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}