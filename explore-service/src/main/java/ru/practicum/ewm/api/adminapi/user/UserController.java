package ru.practicum.ewm.api.adminapi.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.adminapi.user.dto.NewUserRequest;
import ru.practicum.ewm.api.adminapi.user.dto.UserDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("POST /admin/users newUserRequest={}", newUserRequest);
        UserDto userDto = userService.addUser(newUserRequest);
        log.info("POST /admin/users result={}", userDto);
        return userDto;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> ids,
                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                 @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET /admin/users ids={}, from={}, size={}", ids, from, size);
        List<UserDto> userDtos = userService.getUsers(ids, from, size);
        log.info("GET /admin/users result={}", userDtos);
        return userDtos;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.info("DELETE /admin/users userId={}", userId);
        userService.deleteUser(userId);
        log.info("DELETE /admin/users result=");
    }
}
