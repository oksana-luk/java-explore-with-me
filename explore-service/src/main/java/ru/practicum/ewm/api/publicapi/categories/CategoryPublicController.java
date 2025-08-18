package ru.practicum.ewm.api.publicapi.categories;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryPublicService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") @Min(0) int from,
                                           @RequestParam(name = "size", defaultValue = "10") @Positive int size) {

        log.info("GET /categories from={}, size={}", from, size);
        List<CategoryDto> categories = categoryService.getCategories(from, size);
        log.info("GET /categories result={}", categories);
        return categories;
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@PathVariable long categoryId) {
        log.info("GET /categories/{categoryId} categoryId={}", categoryId);
        CategoryDto category = categoryService.getCategory(categoryId);
        log.info("GET /categories/{categoryId} result={}", category);
        return category;
    }
}
