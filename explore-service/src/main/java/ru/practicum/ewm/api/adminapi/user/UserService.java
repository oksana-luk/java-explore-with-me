package ru.practicum.ewm.api.adminapi.user;

import ru.practicum.ewm.api.adminapi.user.dto.NewUserRequest;
import ru.practicum.ewm.api.adminapi.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(long userId);
}
