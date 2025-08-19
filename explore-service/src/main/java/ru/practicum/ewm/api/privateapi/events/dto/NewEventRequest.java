package ru.practicum.ewm.api.privateapi.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.api.privateapi.events.model.Location;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class NewEventRequest {
    @NotNull(message = "Category should not be empty")
    private long category;

    @NotNull(message = "Event date should not be empty")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotBlank(message = "Title should not be empty")
    @Size(min = 3, max = 120, message = "Title should contain from 3 to 120 characters")
    private String title;

    @NotBlank(message = "Description should not be empty")
    @Size(min = 20, max = 7000, message = "Description should contain from 20 to 7000 characters")
    private String description;

    @NotBlank(message = "Annotation should not be empty")
    @Size(min = 20, max = 2000, message = "Annotation should contain from 20 to 2000 characters")
    private String annotation;

    private boolean requestModeration = true;

    @Min(value = 0, message = "Participant limit should be 0 or more")
    private int participantLimit = 0;

    @NotNull(message = "Location should not be empty")
    private Location location;

    private boolean paid = false;
}
