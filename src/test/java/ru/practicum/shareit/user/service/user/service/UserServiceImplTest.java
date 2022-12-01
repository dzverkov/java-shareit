package ru.practicum.shareit.user.service.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private List<User> users;

    @BeforeEach
    void init() {
        users = Arrays.asList(
                new User(1L, "User1", "user1@mail.ru"),
                new User(2L, "User2", "user2@mail.ru"),
                new User(3L, "User3", "user3@mail.ru")
        );
    }


    @Test
    void getUsers() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findAll()).thenReturn(users);

        UserServiceImpl userService = new UserServiceImpl(userRepository);
        List<UserDto> usersRes = userService.getUsers();

        assertEquals(3, usersRes.size());
        assertEquals(users.get(0).getId(), usersRes.get(0).getId());
        assertEquals(users.get(0).getName(), usersRes.get(0).getName());
        assertEquals(users.get(0).getEmail(), usersRes.get(0).getEmail());
    }

    @Test
    void getUserById() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        UserServiceImpl userService = new UserServiceImpl(userRepository);
        UserDto usersRes = userService.getUserById(1L);

        assertEquals(users.get(0).getId(), usersRes.getId());
        assertEquals(users.get(0).getName(), usersRes.getName());
        assertEquals(users.get(0).getEmail(), usersRes.getEmail());

        assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(10L));
    }

    @Test
    void addUser() {

        User user1 = new User(1L, "User1", "user1@mail.ru");

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.save(user1)).thenReturn(user1);

        UserServiceImpl userService = new UserServiceImpl(userRepository);

        assertThrows(ValidationException.class,
                () -> userService.addUser(new UserDto(null, "User1", null)));

        assertThrows(ValidationException.class,
                () -> userService.addUser(new UserDto(null, "User1", "")));

        assertThrows(ValidationException.class,
                () -> userService.addUser(new UserDto(null, "User1", "user1")));

        UserDto userDto = userService.addUser(new UserDto(1L, "User1", "user1@mail.ru"));

        assertEquals(1L, userDto.getId());
        assertEquals("User1", userDto.getName());
        assertEquals("user1@mail.ru", userDto.getEmail());

    }

    @Test
    void updateUser() {
        UserRepository userRepository = mock(UserRepository.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));

        User user2 = new User(1L, "UpdatedUser1", "upduser1@mail.ru");
        when(userRepository.save(user2)).thenReturn(user2);

        UserServiceImpl userService = new UserServiceImpl(userRepository);

        assertThrows(ValidationException.class,
                () -> userService.updateUser(new UserDto(null, "User1", ""), 1L));

        assertThrows(ValidationException.class,
                () -> userService.updateUser(new UserDto(null, "User1", "user1"), 1L));

        UserDto userDto = userService.updateUser(new UserDto(user2.getId(), user2.getName(), user2.getEmail()), 1L);

        assertEquals(user2.getId(), userDto.getId());
        assertEquals(user2.getName(), userDto.getName());
        assertEquals(user2.getEmail(), userDto.getEmail());
    }

    @Test
    void deleteUserById() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(users.get(0)));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        UserServiceImpl userService = new UserServiceImpl(userRepository);

        assertDoesNotThrow(
                () -> userService.deleteUserById(1L)
        );

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUserById(10L));
    }
}