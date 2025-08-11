package ru.practicum.ewm.api.adminapi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Email should not be empty")
    @EmailParts
    private String email;

    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 250)
    private String name;
}
