package ru.practicum.ewm.api.adminapi.compilations;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.adminapi.compilations.dto.CompilationDto;
import ru.practicum.ewm.api.adminapi.compilations.dto.NewCompilationRequest;
import ru.practicum.ewm.api.adminapi.compilations.dto.UpdateCompilationRequest;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationAdminService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationRequest newCompilationRequest) {
        log.info("POST /admin/compilations newCompilationRequest={}", newCompilationRequest);
        CompilationDto compilationDto = compilationService.addCompilation(newCompilationRequest);
        log.info("POST /admin/compilations result count of events in compilation = {}", compilationDto.getEvents().size());
        return compilationDto;
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compilationId) {
        log.info("DELETE /admin/compilations/{compilationId}  compilationId={}", compilationId);
        compilationService.deleteCompilation(compilationId);
        log.info("DELETE /admin/compilations/{compilationId}  successfully ended, compilationId={}", compilationId);
    }

    @PatchMapping("/{compilationId}")
    public CompilationDto updateCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                            @PathVariable long compilationId) {
        log.info("PATCH /admin/compilations/{compilationId} updateCompilationRequest={}, compilationId={}", updateCompilationRequest, compilationId);
        CompilationDto compilationDto = compilationService.updateCompilation(updateCompilationRequest, compilationId);
        log.info("PATCH /admin/compilations/{compilationId} successfully ended, result count of events in compilation ={}",
                compilationDto.getEvents().size());
        return compilationDto;
    }
}
