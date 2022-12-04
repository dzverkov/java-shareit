package ru.practicum.shareit.booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.booking.exception.BookingWrongStateException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestBody @Valid BookingParamDto bookingDto,
													   @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
		log.info("Получен запрос на добавление вещи пользователем , userId={}.", userId);
		if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
			throw new ValidationException("Дата начала бронирования больше даты окончания.");
		}
		return bookingClient.addBooking(bookingDto, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
														   @RequestParam Boolean approved,
														   @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
		log.info("Получен запрос на подтверждение или отклонение вещи по запросу bookingId={} от пользователя userId={}.",
				bookingId, userId);
		return bookingClient.approveBooking(bookingId, approved, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
														   @RequestHeader(name = "X-Sharer-User-Id") Long userId) {
		log.info("Получен запрос на получение данных о бронировании вещи c bookingId={} от пользователя userId={}.",
				bookingId, userId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookingsByUserId(
			@RequestParam(required = false, defaultValue = "ALL") String state,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Получен запрос на получение данных о бронированиях пользователя userId={} с парметром {}.",
				userId, state);
		try {
			BookingSearchState.valueOf(state);
		} catch (IllegalArgumentException e) {
			throw new BookingWrongStateException(String.format("Unknown state: %s", state));
		}
		return bookingClient.getBookingsByUserId(state, userId, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsItemsByUserId(
			@RequestParam(required = false, defaultValue = "ALL") String state,
			@RequestHeader(name = "X-Sharer-User-Id") Long userId,
			@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
			@RequestParam(defaultValue = "10") @Positive Integer size
	) {
		log.info("Получен запрос на получение данных о бронировании вещей пользователя userId={} с парметром {}.",
				userId, state);
		try {
			BookingSearchState.valueOf(state);
		} catch (IllegalArgumentException e) {
			throw new BookingWrongStateException(String.format("Unknown state: %s", state));
		}
		return bookingClient.getBookingsItemsByUserId(state, userId, from, size);
	}
}