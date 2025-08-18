package ru.practicum.ewm.api.adminapi.categories;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryAdminService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryRequest newCategoryRequest) {
        log.info("POST /admin/categories newCategoryRequest={}", newCategoryRequest);
        CategoryDto categoryDto = categoryService.addCategory(newCategoryRequest);
        log.info("POST /admin/categories result={}", categoryDto);
        return categoryDto;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long categoryId) {
        log.info("DELETE /admin/categories/{categoryId} categoryId={}", categoryId);
        categoryService.deleteCategory(categoryId);
        log.info("DELETE /admin/categories{categoryId} successfully ended, categoryId={}", categoryId);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateCategory(@Valid @RequestBody CategoryRequest updateCategoryRequest,
                                      @PathVariable long categoryId) {
        log.info("PATCH /admin/categories/{categoryId} updateCategoryRequest={}", updateCategoryRequest);
        CategoryDto categoryDto = categoryService.updateCategory(updateCategoryRequest, categoryId);
        log.info("PATCH /admin/categories/{categoryId} result={}", categoryDto);
        return categoryDto;
    }
}
