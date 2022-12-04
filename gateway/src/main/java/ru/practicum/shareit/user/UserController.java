package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получен запрос на получение всех пользователей.");
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Получен запрос на получение пользователя с id = {}.", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserDto userDto) {
        log.info("Получен запрос на добавление пользователя.");
        validateNewUser(userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен запрос на обновление пользователя с id = {}.", userId);
        validateUserFields(userDto);
        return userClient.updateUser(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}.", userId);
        return userClient.deleteUserById(userId);
    }


    private void validateNewUser(UserDto user) {
        // Проверяем наличие обязательных полей
        if (user.getEmail() == null) {
            throw new ValidationException("Значение email = null.");
        }
        // Проверяем поля на форматирование
        validateUserFields(user);
    }

    private void validateUserFields(UserDto user) {

        if (user.getEmail() != null) {
            boolean isValidEmail = Pattern.compile("^(.+)@(\\S+)$")
                    .matcher(user.getEmail())
                    .matches();
            if (!isValidEmail) {
                throw new ValidationException("Значение email не валидно.");
            }
        }
    }
}
