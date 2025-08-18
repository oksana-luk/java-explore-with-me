package ru.practicum.ewm.api.adminapi.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.user.dto.NewUserRequest;
import ru.practicum.ewm.api.adminapi.user.dto.UserDto;
import ru.practicum.ewm.api.adminapi.user.model.User;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = userMapper.mapNewUserRequestToUser(newUserRequest);
        log.info("User admin service, adding user: user={}", user);
        user = userRepository.save(user);
        return userMapper.mapUserToUserDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        log.info("User admin service, getting users: page={}", page);
        Page<User> users;
        if (Objects.isNull(ids) || ids.isEmpty()) {
           users = userRepository.findAll(page);
        } else {
           users = userRepository.findByIdIn(ids, page);
        }
        return users.map(userMapper::mapUserToUserDto)
                .getContent();
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        validateNotFound(userId);
        log.info("User admin service, deleting user by id: userId={}", userId);
        userRepository.deleteById(userId);
    }

    private void validateNotFound(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id %d not found", userId)));
    }
}
