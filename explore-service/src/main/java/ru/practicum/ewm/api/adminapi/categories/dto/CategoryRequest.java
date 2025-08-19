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
    @Size(min = 1, max = 50, message = "Name should contain from 1 to 50 characters")
    private String name;
}
