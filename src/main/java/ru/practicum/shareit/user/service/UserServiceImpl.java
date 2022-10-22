package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserEmailIsNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private long id = 0;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userDao.getUsers();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {

        User user = userDao.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        validateUser(userDto);

        userDto.setId(getNextId());
        return UserMapper.toUserDto(userDao.addUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        UserDto existUserDto = getUserById(userId);
        if (existUserDto == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        mergeFields(userDto, existUserDto);
        validateUser(existUserDto);

        return UserMapper.toUserDto(userDao.updateUser(UserMapper.toUser(existUserDto)));
    }

    @Override
    public void deleteUserById(Long userId) {
        if (userDao.getUserById(userId) == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        userDao.deleteUserById(userId);
    }

    private void validateUser(UserDto user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Значение email = null.");
        }

        boolean isValidEmail = Pattern.compile("^(.+)@(\\S+)$")
                .matcher(user.getEmail())
                .matches();
        if (!isValidEmail) {
            throw new ValidationException("Значение email не валидно.");
        }

        Optional<User> userWithSameEmail = userDao.getUsers().stream().filter(
                (u) -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId())
        ).findFirst();
        if (userWithSameEmail.isPresent()) {
            throw new UserEmailIsNotUnique("Адрес почты не уникален.");
        }
    }

    private long getNextId() {
        return ++id;
    }

    private void mergeFields(UserDto source, UserDto target) {
        if (source.getName() != null) target.setName(source.getName());
        if (source.getEmail() != null) target.setEmail(source.getEmail());
    }
}