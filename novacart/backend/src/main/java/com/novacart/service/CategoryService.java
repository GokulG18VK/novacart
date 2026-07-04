package com.novacart.service;

import com.novacart.model.Category;
import com.novacart.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** Business logic for product categories. */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
