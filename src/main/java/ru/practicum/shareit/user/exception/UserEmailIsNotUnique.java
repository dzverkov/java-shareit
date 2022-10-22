package ru.practicum.shareit.user.exception;

public class UserEmailIsNotUnique extends RuntimeException {
    public UserEmailIsNotUnique(String message) {
        super(message);
    }
}
