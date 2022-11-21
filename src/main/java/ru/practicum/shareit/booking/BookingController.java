package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.dto.BookingResultDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public ResponseEntity<BookingResultDto> addBooking(@RequestBody @Valid BookingParamDto bookingDto,
                                                       @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на добавление вещи пользователем , userId={}.", userId);
        return ResponseEntity.ok(bookingService.addBooking(bookingDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResultDto> approveBooking(@PathVariable Long bookingId,
                                                           @RequestParam Boolean approved,
                                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на подтверждение или отклонение вещи по запросу bookingId={} от пользователя userId={}.",
                bookingId, userId);
        return ResponseEntity.ok(bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResultDto> getBookingById(@PathVariable Long bookingId,
                                                           @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение данных о бронировании вещи c bookingId={} от пользователя userId={}.",
                bookingId, userId);
        return ResponseEntity.ok(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public ResponseEntity<List<BookingResultDto>> getBookingsByUserId(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Получен запрос на получение данных о бронированиях пользователя userId={} с парметром {}.",
                userId, state);
        return ResponseEntity.ok(bookingService.getBookingsByUserId(state, userId, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResultDto>> getBookingsItemsByUserId(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader(name = "X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("Получен запрос на получение данных о бронировании вещей пользователя userId={} с парметром {}.",
                userId, state);
        return ResponseEntity.ok(bookingService.getBookingsItemsByUserId(state, userId, from, size));
    }
}
