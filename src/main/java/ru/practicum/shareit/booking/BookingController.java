package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResultDto addBooking(@RequestBody @Valid BookingParamDto bookingDto,
                                       @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи пользователем , userId={}.", userId);
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResultDto approveBooking(@PathVariable Long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на подтверждение или отклонение вещи по запросу bookingId={} от пользователя userId={}.",
                bookingId, userId);
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResultDto getBookingById(@PathVariable Long bookingId,
                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение данных о бронировании вещи c bookingId={} от пользователя userId={}.",
                bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResultDto> getBookingsByUserId(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                      @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение данных о бронированиях пользователя userId={} с парметром {}.",
                userId, state);
        return bookingService.getBookingsByUserId(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingResultDto> getBookingsItemsByUserId(@RequestParam(required = false, defaultValue = "ALL") String state,
                                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение данных о бронировании вещей пользователя userId={} с парметром {}.",
                userId, state);
        return bookingService.getBookingsItemsByUserId(state, userId);
    }
}
