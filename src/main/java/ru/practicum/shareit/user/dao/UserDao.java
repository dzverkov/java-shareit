package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserDao {

    List<User> getUsers();

    User getUserById(Long userId);

    User addUser(User toUser);

    User updateUser(User toUser);

    void deleteUserById(Long userId);
}
