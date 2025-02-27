package com.vinaacademy.platform.feature.category.service;

import com.vinaacademy.platform.exception.BadRequestException;
import com.vinaacademy.platform.feature.category.Category;
import com.vinaacademy.platform.feature.category.dto.CategoryDto;
import com.vinaacademy.platform.feature.category.dto.CategoryRequest;
import com.vinaacademy.platform.feature.category.mapper.CategoryMapper;
import com.vinaacademy.platform.feature.category.repository.CategoryRepository;
import com.vinaacademy.platform.feature.category.utils.CategoryUtils;
import com.vinaacademy.platform.feature.common.utils.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
        String slug = StringUtils.isBlank(request.getSlug())
                ? request.getSlug() : SlugUtils.toSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            throw BadRequestException.message("Slug đã tồn tại");
        }

        Category parent = null;
        if (StringUtils.isNotBlank(request.getParentSlug())) {
            parent = categoryRepository.findBySlug(request.getParentSlug())
                    .orElseThrow(() -> BadRequestException.message("Danh mục cha không tồn tại"));
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .parent(parent)
                .build();

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto updateCategory(String slug, CategoryRequest request) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));

        String newSlug = StringUtils.isBlank(request.getSlug())
                ? request.getSlug() : SlugUtils.toSlug(request.getName());

        if (!slug.equals(newSlug) && categoryRepository.existsBySlug(newSlug)) {
            throw BadRequestException.message("Slug đã tồn tại");
        }

        Category parent = null;
        if (StringUtils.isNotBlank(request.getParentSlug())) {
            parent = categoryRepository.findBySlug(request.getParentSlug())
                    .orElseThrow(() -> BadRequestException.message("Danh mục cha không tồn tại"));

            // Check if parent is child of category
            if (CategoryUtils.isParent(category, parent)) {
                throw BadRequestException.message("Danh mục cha không hợp lệ");
            }
        }

        category.setName(request.getName());
        category.setSlug(newSlug);
        category.setParent(parent);

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));
        if (categoryRepository.existsByParent(category)) {
            throw BadRequestException.message("Danh mục có danh mục con");
        }
        if (categoryRepository.existsByCourses(category)) {
            throw BadRequestException.message("Danh mục có khóa học");
        }

        categoryRepository.delete(category);
    }
}
