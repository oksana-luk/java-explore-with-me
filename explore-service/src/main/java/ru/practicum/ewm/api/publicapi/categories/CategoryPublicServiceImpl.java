package ru.practicum.ewm.api.publicapi.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.api.adminapi.categories.CategoryMapper;
import ru.practicum.ewm.api.adminapi.categories.CategoryRepository;
import ru.practicum.ewm.api.adminapi.categories.dto.CategoryDto;
import ru.practicum.ewm.api.adminapi.categories.model.Category;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryPublicServiceImpl implements CategoryPublicService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        PageRequest pageRequest = PageRequest.of((from > 0) ? from / size : 0, size);
        log.info("Category public service, getting categories: page={}", pageRequest);
        return categoryRepository.findAll(pageRequest).stream()
                .map(categoryMapper::mapCategoryToCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategory(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%s was not found", categoryId)));
        return categoryMapper.mapCategoryToCategoryDto(category);
    }
}
