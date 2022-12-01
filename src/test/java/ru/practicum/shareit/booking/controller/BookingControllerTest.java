package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    BookingParamDto bookingParamDto = new BookingParamDto(
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMonths(1),
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMonths(2),
            1L, 1L, BookingStatus.WAITING);
    BookingResultDto bookingResultDto = new BookingResultDto(
            1L, bookingParamDto.getStart(), bookingParamDto.getEnd(),
            new BookingResultDto.ItemDto(1L, "Item 1"),
            new BookingResultDto.UserDto(1L),
            bookingParamDto.getStatus()
    );

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(Mockito.any(BookingParamDto.class), eq(1L)))
                .thenReturn(bookingResultDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingParamDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResultDto)));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(bookingResultDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResultDto)));
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(bookingResultDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingResultDto)));
    }

    @Test
    void getBookingsByUserId() throws Exception {
        when(bookingService.getBookingsByUserId(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.singletonList(bookingResultDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Collections.singletonList(bookingResultDto))));
    }

    @Test
    void getBookingsItemsByUserId() throws Exception {
        when(bookingService.getBookingsItemsByUserId(Mockito.anyString(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Collections.singletonList(bookingResultDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(bookingResultDto.getId()), Long.class))
//                .andExpect(jsonPath("$[0].start", is(bookingResultDto.getStart().toString())))
//                .andExpect(jsonPath("$[0].end", is(bookingResultDto.getEnd().toString())))
//                .andExpect(jsonPath("$[0].item.id", is(bookingResultDto.getItem().getId()), Long.class))
//                .andExpect(jsonPath("$[0].item.name", is(bookingResultDto.getItem().getName())))
//                .andExpect(jsonPath("$[0].booker.id", is(bookingResultDto.getBooker().getId()), Long.class))
                .andExpect(content().json(mapper.writeValueAsString(Collections.singletonList(bookingResultDto))));
    }
}