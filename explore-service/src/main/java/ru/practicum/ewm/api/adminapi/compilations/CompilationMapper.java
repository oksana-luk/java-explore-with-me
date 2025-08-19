package ru.practicum.ewm.api.adminapi.compilations;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.api.adminapi.compilations.dto.CompilationDto;
import ru.practicum.ewm.api.adminapi.compilations.dto.NewCompilationRequest;
import ru.practicum.ewm.api.adminapi.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.api.adminapi.compilations.model.Compilation;
import ru.practicum.ewm.api.privateapi.events.dto.EventShortDto;
import ru.practicum.ewm.api.privateapi.events.model.Event;

import java.util.Set;

@Component
public class CompilationMapper {
    Compilation mapNewCompilationRequestToCompilation(NewCompilationRequest newCompilationRequest, Set<Event> events) {
        return new Compilation(
                0L,
                newCompilationRequest.isPinned(),
                newCompilationRequest.getTitle(),
                events
        );
    }

    public void updateFields(Compilation compilation, UpdateCompilationRequest updateCompilationRequest, Set<Event> events) {
        if (updateCompilationRequest.hasPinned()) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.hasTitle()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.hasEvents()) {
            compilation.setEvents(events);
        }
    }

    public CompilationDto mapCompilationToCompilationDto(Compilation compilation, Set<EventShortDto> eventShortDto) {
        return new CompilationDto(
                compilation.getId(),
                compilation.isPinned(),
                compilation.getTitle(),
                eventShortDto
        );
    }
}
