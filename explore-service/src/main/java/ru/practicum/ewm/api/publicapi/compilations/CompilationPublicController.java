package ru.practicum.ewm.api.publicapi.compilations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.adminapi.compilations.dto.CompilationDto;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationPublicService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                @RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                                @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("GET /compilations pinned={}, from={}, size={}", pinned, from, size);
        List<CompilationDto> compilationDtos = compilationService.getCompilations(pinned, from, size);
        log.info("GET /compilations result={}", compilationDtos);
        return compilationDtos;
    }

    @GetMapping("/{compilationId}")
    public CompilationDto getCompilation(@PathVariable long compilationId) {
        log.info("GET /compilations/{compilationId} compilationId={}", compilationId);
        CompilationDto compilationDto = compilationService.getCompilation(compilationId);
        log.info("GET /compilations/{compilationId} result={}", compilationDto);
        return compilationDto;
    }
}
