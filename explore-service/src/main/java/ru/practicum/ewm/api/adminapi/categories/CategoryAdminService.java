package ru.practicum.ewm.api.adminapi.categories;

import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryRequest;

public interface CategoryAdminService {
    CategoryDto addCategory(CategoryRequest newCategoryRequest);

    CategoryDto updateCategory(CategoryRequest updateCategoryRequest, long categoryId);

    void deleteCategory(long categoryId);
}
