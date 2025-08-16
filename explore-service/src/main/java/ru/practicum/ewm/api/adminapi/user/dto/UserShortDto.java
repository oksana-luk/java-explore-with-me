package ru.practicum.ewm.api.adminapi.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserShortDto {
    private long id;
    private String name;
}
