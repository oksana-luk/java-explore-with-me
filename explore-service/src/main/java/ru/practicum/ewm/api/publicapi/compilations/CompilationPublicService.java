package ru.practicum.ewm.api.publicapi.compilations;

import ru.practicum.ewm.api.adminapi.compilations.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilation(long compilationId);
}
