package ru.practicum.ewm.api.adminapi.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserDto {
    private String email;
    private long id;
    private String name;
}
