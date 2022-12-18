package ru.practicum.shareit.booking.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.ErrorResponse;

@RestControllerAdvice
public class BookingExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingWrongStateException(final BookingWrongStateException e) {
        return new ErrorResponse(e.getMessage());
    }
}
