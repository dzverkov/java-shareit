package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(mapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {

        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        return mapper.toUserDto(user.get());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        validateUser(userDto);

        return mapper.toUserDto(userRepository.save(mapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        UserDto existUserDto = getUserById(userId);

        mergeFields(userDto, existUserDto);
        validateUser(existUserDto);

        return mapper.toUserDto(userRepository.save(mapper.toUser(existUserDto)));
    }

    @Override
    public void deleteUserById(Long userId) {
        // Проверяем наличие пользователя, иначе будет выброшено UserNotFoundException
        getUserById(userId);
        userRepository.deleteById(userId);
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
    }

    private void mergeFields(UserDto source, UserDto target) {
        if (source.getName() != null) target.setName(source.getName());
        if (source.getEmail() != null) target.setEmail(source.getEmail());
    }
}
