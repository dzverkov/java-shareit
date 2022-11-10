package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;

import java.util.List;

public interface BookingService {


    BookingResultDto addBooking(BookingParamDto bookingDto, Long userId);

    BookingResultDto approveBooking(Long bookingId, Boolean approved, Long userId);

    BookingResultDto getBookingById(Long bookingId, Long userId);

    List<BookingResultDto> getBookingsByUserId(String state, Long userId);

    List<BookingResultDto> getBookingsItemsByUserId(String state, Long userId);
}
