package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper
public interface UserMapper {
    UserDto toUserDto(User user);

    User toUser(UserDto userDto);
}
