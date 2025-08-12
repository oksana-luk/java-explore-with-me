package ru.practicum.ewm.api.adminapi.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.api.adminapi.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
