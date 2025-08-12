package ru.practicum.ewm.api.adminapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.ewm.api.adminapi.user.model.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Email should not be empty")
    @EmailParts
    private String email;

    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 250)
    private String name;

    private User.Role role = User.Role.USER;
}
