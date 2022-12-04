package ru.practicum.shareit.booking.exception;

public class ApproveNotOwnerException extends RuntimeException {
    public ApproveNotOwnerException(String message) {
        super(message);
    }
}
