package ru.practicum.shareit.booking.exception;

public class BookingWrongStateException extends RuntimeException {
    public BookingWrongStateException(String message) {
        super(message);
    }
}
