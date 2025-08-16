package ru.practicum.ewm.api.adminapi.categories.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CategoryDto {
    private long id;
    private String name;
}
