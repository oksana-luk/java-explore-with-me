package ru.practicum.ewm.api.adminapi.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Name should not be empty")
    @Size(max = 50)
    private String name;
}
