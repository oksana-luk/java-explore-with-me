package ru.practicum.ewm.api.adminapi.categories;

import org.mapstruct.Mapper;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryRequest;
import ru.practicum.ewm.api.adminapi.categories.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category mapCategoryRequestToCategory(CategoryRequest categoryRequest);

    CategoryDto mapCategoryToCategoryDto(Category category);
}
