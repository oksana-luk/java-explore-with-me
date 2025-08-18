package ru.practicum.ewm.api.publicapi.categories;

import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;

import java.util.List;

public interface CategoryPublicService {
    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(long categoryId);
}
