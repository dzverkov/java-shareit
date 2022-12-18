package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(Long userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    void deleteUserById(Long userId);
}
