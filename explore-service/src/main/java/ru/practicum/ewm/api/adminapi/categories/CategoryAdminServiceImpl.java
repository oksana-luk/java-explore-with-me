package ru.practicum.ewm.api.adminapi.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryRequest;
import ru.practicum.ewm.api.adminapi.categories.model.Category;
import ru.practicum.ewm.api.privateapi.events.EventRepository;
import ru.practicum.ewm.exception.ActionConflictException;
import ru.practicum.ewm.exception.NotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(CategoryRequest newCategoryRequest) {
        Category category = categoryMapper.mapCategoryRequestToCategory(newCategoryRequest);
        log.info("Category admin service, adding category {}", category);
        category = categoryRepository.save(category);
        return categoryMapper.mapCategoryToCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(CategoryRequest updateCategoryRequest, long categoryId) {
        Category category = categoryMapper.mapCategoryRequestToCategory(updateCategoryRequest);
        log.info("Category admin service, updating category {}", category);

        Category oldCategory = validationNotFound(categoryId);

        if (!category.getName().equals(oldCategory.getName())) {
            oldCategory.setName(category.getName());
            category = categoryRepository.save(oldCategory);
        }
        return categoryMapper.mapCategoryToCategoryDto(category);
    }

    @Override
    public void deleteCategory(long categoryId) {
        validationNotFound(categoryId);
        validationCategoryHasEvents(categoryId);
        log.info("Category admin service, deleting category by id {}", categoryId);
        categoryRepository.deleteById(categoryId);
    }

    private Category validationNotFound(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found", categoryId)));
    }

    private void validationCategoryHasEvents(long categoryId) {
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ActionConflictException(String.format("The category with id %d is not empty", categoryId));
        }
    }
}
