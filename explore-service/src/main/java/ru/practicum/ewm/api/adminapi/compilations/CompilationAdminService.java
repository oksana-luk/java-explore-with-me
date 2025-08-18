package ru.practicum.ewm.api.adminapi.compilations;

import ru.practicum.ewm.api.adminapi.compilations.dto.CompilationDto;
import ru.practicum.ewm.api.adminapi.compilations.dto.NewCompilationRequest;
import ru.practicum.ewm.api.adminapi.compilations.dto.UpdateCompilationRequest;

public interface CompilationAdminService {
    CompilationDto addCompilation(NewCompilationRequest newCompilationRequest);

    void deleteCompilation(long compilationId);

    CompilationDto updateCompilation(UpdateCompilationRequest updateCompilationRequest, long compilationId);
}
