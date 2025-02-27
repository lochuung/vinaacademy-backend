package com.vinaacademy.platform.feature.category.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.dto.CategoryDto;
import com.vinaacademy.platform.feature.category.dto.CategoryRequest;
import com.vinaacademy.platform.feature.category.mapper.CategoryMapper;
import com.vinaacademy.platform.feature.category.repository.CategoryRepository;
import com.vinaacademy.platform.feature.category.utils.CategoryUtils;
import com.vinaacademy.platform.feature.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategories() {
        // Root categories
        List<Category> categories = categoryRepository.findAllRootCategoriesWithChildren();

        return categories.stream()
                .map(v -> CategoryUtils.buildCategoryHierarchy(v, categoryMapper))
                .toList();
    }

    @Override
    public CategoryDto getCategory(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));

        return CategoryUtils.buildCategoryHierarchy(category, categoryMapper);
    }

    @Override
    public CategoryDto createCategory(CategoryRequest request) {
        return null;
    }

    @Override
    public CategoryDto updateCategory(String slug, CategoryRequest request) {
        return null;
    }

    @Override
    public void deleteCategory(String slug) {

    }
}
