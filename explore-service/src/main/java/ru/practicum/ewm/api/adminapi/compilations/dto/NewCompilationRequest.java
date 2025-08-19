package ru.practicum.ewm.api.adminapi.compilations.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewCompilationRequest {
    private  boolean pinned = false;
    @NotBlank(message = "Title should not be empty")
    @Size(min = 1, max = 50, message = "Title should contain from 1 to 50 characters")
    private String title;
    private Set<Long> events = new HashSet<>();
}
