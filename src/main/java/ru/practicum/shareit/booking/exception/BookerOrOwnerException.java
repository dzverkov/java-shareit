package ru.practicum.shareit.booking.exception;

public class BookerOrOwnerException extends RuntimeException {
    public BookerOrOwnerException(String message) {
        super(message);
    }
}
