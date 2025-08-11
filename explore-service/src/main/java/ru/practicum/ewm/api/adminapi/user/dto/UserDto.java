package ru.practicum.ewm.api.adminapi.user.dto;

import lombok.*;

@Data
public class UserDto {
    private String email;
    private long id;
    private String name;
}
