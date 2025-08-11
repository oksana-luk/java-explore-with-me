package ru.practicum.ewm.api.adminapi.user;

import org.mapstruct.Mapper;
import ru.practicum.ewm.api.adminapi.user.dto.NewUserRequest;
import ru.practicum.ewm.api.adminapi.user.dto.UserDto;
import ru.practicum.ewm.api.adminapi.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User mapNewUserRequestToUser(NewUserRequest newUserRequest);

    UserDto mapUserToUserDto(User user);
}
