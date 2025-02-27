package com.vinaacademy.platform.feature.category.utils;

import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.dto.CategoryDto;
import com.vinaacademy.platform.feature.category.mapper.CategoryMapper;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class CategoryUtils {
    public static CategoryDto buildCategoryHierarchy(Category category, CategoryMapper mapper) {
        CategoryDto categoryDto = mapper.toDto(category);

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryDto> childrenDto = category.getChildren().stream()
                    .map(v -> CategoryUtils.buildCategoryHierarchy(v, mapper))
                    .toList();

            categoryDto.setChildren(childrenDto);
        } else {
            categoryDto.setChildren(List.of());
        }

        return categoryDto;
    }

    public static boolean isParent(Category parent, Category child) {
        if (child.getParent() == null) {
            return false;
        }

        if (child.getParent().getId().equals(parent.getId())) {
            return true;
        }

        return isParent(parent, child.getParent());
    }
}
