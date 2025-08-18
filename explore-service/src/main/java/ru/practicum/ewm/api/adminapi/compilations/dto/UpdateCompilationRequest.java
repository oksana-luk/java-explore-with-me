package ru.practicum.ewm.api.adminapi.compilations.dto;

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
public class UpdateCompilationRequest {
    private  Boolean pinned;
    @Size(min = 1, max = 50, message = "Title should contain from 1 to 50 characters")
    private String title;
    private Set<Long> events = new HashSet<>();

    public boolean hasTitle() {
        return ! (title == null || title.isBlank());
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasEvents() {
        return ! (events == null || events.isEmpty());
    }
}

